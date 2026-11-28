package br.edu.ifto.pwebII;

import br.edu.ifto.pwebII.model.jdbc.repository.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureMockMvc
class PwebIiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AgendaRepository agendaRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void deveExecutarConsultaDeDuplicidadeDaAgenda() {
		assertFalse(agendaRepository.existeHorarioDoMedico(6L,
				java.time.LocalDate.of(2027, 1, 5), java.time.LocalTime.of(8, 0)));
	}


	@Test
	void deveExibirErrosAoCadastrarPacienteInvalido() throws Exception {
		mockMvc.perform(post("/paciente/save")
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
