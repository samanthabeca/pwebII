(() => {
    const campoNome = document.getElementById('nome');
    if (!campoNome) return;

    campoNome.addEventListener('blur', () => {
        campoNome.value = campoNome.value
            .trim()
            .split(/\s+/)
            .filter(Boolean)
            .map((palavra) => palavra.charAt(0).toLocaleUpperCase('pt-BR')
                + palavra.slice(1).toLocaleLowerCase('pt-BR'))
            .join(' ');
    });
})();
