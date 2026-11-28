(() => {
    const especialidadeSelect = document.getElementById('especialidadeId');
    const medicoSelect = document.getElementById('medicoId');
    const avisoMedicoObrigatorio = document.querySelector('[data-medico-obrigatorio]');
    const form = especialidadeSelect?.closest('form');
    if (!especialidadeSelect || !medicoSelect) return;

    const opcaoInicial = medicoSelect.querySelector('option[value=""]');
    const opcoesMedicos = [...medicoSelect.options].filter((opcao) => opcao.value);

    const atualizarMedicos = () => {
        const especialidadeId = especialidadeSelect.value;
        const medicosDaEspecialidade = opcoesMedicos.filter((opcao) =>
            !especialidadeId || opcao.dataset.especialidadeId === especialidadeId);

        opcoesMedicos.forEach((opcao) => {
            const disponivel = medicosDaEspecialidade.includes(opcao);
            opcao.hidden = !disponivel;
            opcao.disabled = !disponivel;
        });

        if (!especialidadeId) {
            opcaoInicial.textContent = 'Todos os médicos';
            avisoMedicoObrigatorio?.classList.add('d-none');
            return;
        }

        opcaoInicial.textContent = '-- Selecione um médico --';
        const medicoSelecionado = medicoSelect.selectedOptions[0];
        if (!medicoSelecionado || medicoSelecionado.disabled) {
            medicoSelect.value = '';
        }

        if (medicosDaEspecialidade.length === 1) {
            medicoSelect.value = medicosDaEspecialidade[0].value;
        }

        if (!avisoMedicoObrigatorio) return;

        if (medicosDaEspecialidade.length > 1 && !medicoSelect.value) {
            avisoMedicoObrigatorio.textContent = 'Selecione um médico para consultar os horários desta especialidade.';
            avisoMedicoObrigatorio.classList.remove('d-none');
        } else {
            avisoMedicoObrigatorio.classList.add('d-none');
        }
    };

    especialidadeSelect.addEventListener('change', atualizarMedicos);
    form?.addEventListener('submit', (event) => {
        if (especialidadeSelect.value && !medicoSelect.value) {
            event.preventDefault();
            avisoMedicoObrigatorio?.classList.remove('d-none');
            medicoSelect.focus();
        }
    });
    atualizarMedicos();
})();
