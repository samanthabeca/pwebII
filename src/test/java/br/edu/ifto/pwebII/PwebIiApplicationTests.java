package br.edu.ifto.pwebII;

import br.edu.ifto.pwebII.model.entity.Agenda;
import br.edu.ifto.pwebII.model.entity.StatusAgenda;
import br.edu.ifto.pwebII.model.jdbc.repository.AgendaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.EspecialidadeRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
import br.edu.ifto.pwebII.service.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class PwebIiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AgendaRepository agendaRepository;

	@Autowired
	private MedicoRepository medicoRepository;

	@Autowired
	private EspecialidadeRepository especialidadeRepository;

	@Autowired
	private UsuarioDetailsService usuarioDetailsService;

	@Test
	void contextLoads() {
	}

	@Test
	void deveExecutarConsultaDeDuplicidadeDaAgenda() {
		assertFalse(agendaRepository.existeHorarioDoMedico(6L,
				java.time.LocalDate.of(2027, 1, 5), java.time.LocalTime.of(8, 0)));
	}

	@Test
	void deveCarregarEspecialidadesDosMedicosIniciais() {
		assertEquals("Dermatologista", medicoRepository.findById(6L).orElseThrow().getEspecialidade().getNome());
		assertEquals("Cardiologista", medicoRepository.findById(7L).orElseThrow().getEspecialidade().getNome());
		assertEquals("Clínico Geral", medicoRepository.findById(8L).orElseThrow().getEspecialidade().getNome());
	}

	@Test
	void deveListarApenasEspecialidadesComMedicos() {
		List<String> especialidades = especialidadeRepository.findComMedicosOrderByNomeAsc().stream()
				.map(especialidade -> especialidade.getNome())
				.toList();

		assertEquals(List.of("Cardiologista", "Clínico Geral", "Dermatologista"), especialidades);
	}

	@Test
	@Transactional
	void deveFiltrarHorariosDisponiveisPorMedicoEspecialidadeEDia() {
		LocalDate amanha = LocalDate.now().plusDays(1);
		Agenda horarioDisponivel = agendaRepository.save(
				criarAgenda(6L, amanha, LocalTime.of(8, 0), StatusAgenda.DISPONIVEL));
		agendaRepository.save(criarAgenda(7L, amanha, LocalTime.of(8, 0), StatusAgenda.DISPONIVEL));
		agendaRepository.save(criarAgenda(6L, amanha, LocalTime.of(9, 0), StatusAgenda.AGENDADO));
		agendaRepository.flush();

		List<Agenda> horarios = agendaRepository.findDisponiveisParaReserva(
				StatusAgenda.DISPONIVEL, LocalDate.now(), LocalTime.now(), 6L,
				medicoRepository.findById(6L).orElseThrow().getEspecialidade().getId_especialidade(), amanha);

		assertEquals(1, horarios.size());
		assertEquals(horarioDisponivel.getMedico().getId(), horarios.getFirst().getMedico().getId());
		assertEquals(LocalTime.of(8, 0), horarios.getFirst().getHorario());

		List<Agenda> horariosDoMedico = agendaRepository.findDisponiveisParaReserva(
				StatusAgenda.DISPONIVEL, LocalDate.now(), LocalTime.now(), 6L, null, null);
		assertEquals(1, horariosDoMedico.size());
	}

	private Agenda criarAgenda(Long medicoId, LocalDate data, LocalTime horario, StatusAgenda status) {
		Agenda agenda = new Agenda();
		agenda.setMedico(medicoRepository.findById(medicoId).orElseThrow());
		agenda.setData(data);
		agenda.setHorario(horario);
		agenda.setStatus(status);
		return agenda;
	}

	@Test
	void deveSelecionarAutomaticamenteOMedicoQuandoEspecialidadePossuirApenasUm() throws Exception {
		mockMvc.perform(get("/agenda/disponiveis")
				.param("especialidadeId", "3")
				.with(user(usuarioDetailsService.loadUserByUsername("sam@mail.com"))))
				.andExpect(status().isOk())
				.andExpect(view().name("agenda/disponiveis"))
				.andExpect(model().attribute("medicoId", 6L))
				.andExpect(model().attribute("selecaoMedicoObrigatoria", false))
				.andExpect(model().attribute("totalHorarios", 0));
	}


	@Test
	void deveExibirErrosAoCadastrarPacienteInvalido() throws Exception {
		mockMvc.perform(post("/paciente/save")
				.with(user(usuarioDetailsService.loadUserByUsername("sam@mail.com")))
				.with(csrf())
				.param("nome", "A")
				.param("cpf", "123")
				.param("email", "email-invalido")
				.param("telefone", "12"))
				.andExpect(status().isOk())
				.andExpect(view().name("paciente/form"))
				.andExpect(model().attributeHasFieldErrors("paciente", "nome", "cpf", "email", "telefone"))
				.andExpect(content().string(containsString("O CPF deve conter exatamente 11 dígitos numéricos.")));
	}

}
