def read_turing_machine_rules(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()

    # Словарь для подсчета вхождений
    transitions = {}

    for line in lines:
        # Разделение строки на части
        parts = line.strip().split(' -> ')
        if len(parts) == 2:
            transition = parts[1]
            if transition in transitions:
                transitions[transition] += 1
            else:
                transitions[transition] = 1

    # Вывод повторяющихся строк
    repeated_transitions = {key: value for key, value in transitions.items() if value > 1}
    return repeated_transitions

# Путь к файлу с правилами машины Тьюринга
file_path = '/home/greygosling/Документы/DM_LAB/turing-machine-visualizer/factorial.out'

# Запуск функции и вывод результатов
repeated_transitions = read_turing_machine_rules(file_path)
for transition, count in repeated_transitions.items():
    print(f'Transition: "{transition}" repeats {count} times')



