def theoretical_spectrum(peptide):
    """
    Вычисляет теоретический спектр пептида.

    Параметры:
    peptide (str): Последовательность пептида.

    Возвращает:
    str: Отсортированный список масс фрагментов.
    """
    # Алфавит аминокислот и их массы
    alphabet = ['A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y']
    masses = [71, 103, 115, 129, 147, 57, 137, 113, 128, 113, 131, 114, 97, 128, 156, 87, 101, 99, 186, 163]
    mass_dict = dict(zip(alphabet, masses))
    
    n = len(peptide)
    prefix_masses = [0]  
    current_mass = 0
    
    # Вычисляем массы префиксов
    for amino_acid in peptide:
        current_mass += mass_dict[amino_acid]
        prefix_masses.append(current_mass)
    
    suffix_masses = []
    # Вычисляем массы суффиксов
    for i in range(n):
        suffix_mass = sum(mass_dict[peptide[j]] for j in range(i, n))
        suffix_masses.append(suffix_mass)

    # Объединяем и сортируем все массы
    total_masses = set(prefix_masses + suffix_masses)
    sorted_masses = sorted(total_masses)
    return ' '.join(map(str, sorted_masses))


# Ввод последовательности пептида
input_peptide = input("Введите последовательность пептида: ")
print("Теоретический спектр:", theoretical_spectrum(input_peptide))
