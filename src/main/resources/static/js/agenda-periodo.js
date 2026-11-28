(() => {
    const calendar = document.querySelector('[data-period-calendar]');
    if (!calendar) return;

    const startInput = document.getElementById('dataInicial');
    const endInput = document.getElementById('dataFinal');
    const daysElement = calendar.querySelector('[data-calendar-days]');
    const monthElement = calendar.querySelector('[data-calendar-month]');
    const summaryElement = calendar.querySelector('[data-calendar-summary]');
    const previousButton = calendar.querySelector('[data-calendar-previous]');
    const nextButton = calendar.querySelector('[data-calendar-next]');
    const clearButton = calendar.querySelector('[data-calendar-clear]');
    const form = calendar.closest('form');
    const scheduleInputs = form.querySelectorAll('input[name="horarios"]');
    const scheduleValidation = form.querySelector('[data-schedule-validation]');
    const formatter = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'long' });
    const monthFormatter = new Intl.DateTimeFormat('pt-BR', { month: 'long', year: 'numeric' });

    const parseDate = (value) => {
        if (!value) return null;
        const [year, month, day] = value.split('-').map(Number);
        return new Date(year, month - 1, day);
    };

    const isoDate = (date) => [date.getFullYear(), String(date.getMonth() + 1).padStart(2, '0'),
        String(date.getDate()).padStart(2, '0')].join('-');

    const sameDate = (first, second) => first && second && isoDate(first) === isoDate(second);
    const isBetween = (date, start, end) => start && end && date >= start && date <= end;
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let start = parseDate(startInput.value);
    let end = parseDate(endInput.value);
    let previewEnd = null;
    let displayedMonth = new Date((start || today).getFullYear(), (start || today).getMonth(), 1);

    const updateInputs = () => {
        startInput.value = start ? isoDate(start) : '';
        endInput.value = end ? isoDate(end) : '';
        startInput.dispatchEvent(new Event('change', { bubbles: true }));
        endInput.dispatchEvent(new Event('change', { bubbles: true }));
    };

    const updateSummary = () => {
        if (!start) {
            summaryElement.textContent = 'Selecione a data inicial no calendário.';
        } else if (!end) {
            summaryElement.textContent = `Início: ${formatter.format(start)}. Selecione a data final.`;
        } else {
            summaryElement.textContent = `${formatter.format(start)} até ${formatter.format(end)}`;
        }
    };

    const selectDate = (date) => {
        if (!start || end) {
            start = date;
            end = null;
        } else if (date < start) {
            end = start;
            start = date;
        } else {
            end = date;
        }
        previewEnd = null;
        updateInputs();
        render();
    };

    const render = () => {
        const visibleEnd = end || previewEnd;
        const rangeStart = start && visibleEnd && visibleEnd < start ? visibleEnd : start;
        const rangeEnd = start && visibleEnd && visibleEnd < start ? start : visibleEnd;
        const firstDay = new Date(displayedMonth.getFullYear(), displayedMonth.getMonth(), 1);
        const gridStart = new Date(firstDay);
        gridStart.setDate(firstDay.getDate() - firstDay.getDay());

        monthElement.textContent = monthFormatter.format(displayedMonth);
        daysElement.replaceChildren();

        for (let index = 0; index < 42; index += 1) {
            const date = new Date(gridStart);
            date.setDate(gridStart.getDate() + index);
            const button = document.createElement('button');
            button.type = 'button';
            button.className = 'period-calendar__day';
            button.textContent = date.getDate();
            button.setAttribute('aria-label', formatter.format(date));
            button.dataset.calendarDate = isoDate(date);

            if (date.getMonth() !== displayedMonth.getMonth()) button.classList.add('period-calendar__day--outside');
            if (date.getDay() === 0 || date.getDay() === 6) button.classList.add('period-calendar__day--weekend');
            if (isBetween(date, rangeStart, rangeEnd)) button.classList.add('period-calendar__day--range');
            if (sameDate(date, start)) button.classList.add('period-calendar__day--start');
            if (sameDate(date, end)) button.classList.add('period-calendar__day--end');
            if (sameDate(date, today)) button.setAttribute('aria-current', 'date');

            if (date < today) {
                button.disabled = true;
                button.classList.add('period-calendar__day--past');
            }
            daysElement.append(button);
        }
        updateSummary();
    };

    previousButton.addEventListener('click', () => {
        displayedMonth = new Date(displayedMonth.getFullYear(), displayedMonth.getMonth() - 1, 1);
        render();
    });
    nextButton.addEventListener('click', () => {
        displayedMonth = new Date(displayedMonth.getFullYear(), displayedMonth.getMonth() + 1, 1);
        render();
    });
    daysElement.addEventListener('click', (event) => {
        const button = event.target.closest('[data-calendar-date]');
        if (!button || button.disabled) return;
        selectDate(parseDate(button.dataset.calendarDate));
    });
    daysElement.addEventListener('mouseover', (event) => {
        const button = event.target.closest('[data-calendar-date]');
        if (!button || button.disabled || !start || end) return;

        const date = parseDate(button.dataset.calendarDate);
        if (!sameDate(date, previewEnd)) {
            previewEnd = date;
            render();
        }
    });
    daysElement.addEventListener('mouseleave', () => {
        if (previewEnd) {
            previewEnd = null;
            render();
        }
    });
    clearButton.addEventListener('click', () => {
        start = null;
        end = null;
        previewEnd = null;
        updateInputs();
        render();
    });

    form.addEventListener('submit', (event) => {
        const possuiHorarioSelecionado = [...scheduleInputs].some((input) => input.checked);
        if (!possuiHorarioSelecionado) {
            event.preventDefault();
            scheduleValidation.classList.remove('d-none');
            scheduleInputs[0]?.focus();
        }
    });
    scheduleInputs.forEach((input) => input.addEventListener('change', () => {
        if (input.checked || [...scheduleInputs].some((item) => item.checked)) {
            scheduleValidation.classList.add('d-none');
        }
    }));

    render();
})();
