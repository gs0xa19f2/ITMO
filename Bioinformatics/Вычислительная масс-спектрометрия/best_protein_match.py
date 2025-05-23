def get_XZ_letters_masses():
    """
    Возвращает массы для аминокислот X и Z (используются для коротких спектров).

    Возвращает:
    dict: Словарь масс для X и Z.
    """
    letters = ['X', 'Z']
    masses = [4, 5]
    return dict(zip(letters, masses))

def get_AA_letters_masses():
    """
    Возвращает стандартные массы аминокислот.

    Возвращает:
    dict: Словарь стандартных масс аминокислот.
    """
    letters = ['A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y']
    masses = [71, 103, 115, 129, 147, 57, 137, 113, 128, 113, 131, 114, 97, 128, 156, 87, 101, 99, 186, 163]
    return dict(zip(letters, masses))

def peptide_mass(peptide, mass_dict):
    """
    Вычисляет массу пептида.

    Параметры:
    peptide (str): Последовательность пептида.
    mass_dict (dict): Словарь масс аминокислот.

    Возвращает:
    int: Масса пептида.
    """
    return sum(mass_dict[aa] for aa in peptide)

def peptide_spectrum(peptide, mass_dict):
    """
    Вычисляет спектр пептида.

    Параметры:
    peptide (str): Последовательность пептида.
    mass_dict (dict): Словарь масс аминокислот.

    Возвращает:
    list: Спектр пептида.
    """
    prefix_mass = [0]
    for aa in peptide:
        prefix_mass.append(prefix_mass[-1] + mass_dict[aa])
    return prefix_mass

def score_peptide(peptide, spectral_vector, mass_dict):
    """
    Вычисляет оценку пептида на основе спектрального вектора.

    Параметры:
    peptide (str): Последовательность пептида.
    spectral_vector (list): Спектральный вектор.
    mass_dict (dict): Словарь масс аминокислот.

    Возвращает:
    int: Оценка пептида.
    """
    peptide_spec = peptide_spectrum(peptide, mass_dict)
    score = 0
    for mass in peptide_spec:
        if mass > 0 and mass <= len(spectral_vector):
            score += spectral_vector[mass-1]
    return score

def best_protein(proteome, spectral_vector, letters, masses):
    """
    Находит лучший пептид, соответствующий спектральному вектору.

    Параметры:
    proteome (str): Аминокислотная последовательность протеома.
    spectral_vector (list): Спектральный вектор.
    letters (list): Список аминокислот.
    masses (list): Список масс аминокислот.

    Возвращает:
    str: Последовательность лучшего пептида.
    """
    mass_dict = dict(zip(letters, masses))
    target_mass = len(spectral_vector)
    best_peptide = ""
    best_score = float('-inf')
    
    for start in range(len(proteome)):
        for end in range(start + 1, len(proteome) + 1):
            peptide = proteome[start:end]
            if peptide_mass(peptide, mass_dict) == target_mass:
                peptide_score = score_peptide(peptide, spectral_vector, mass_dict)
                if peptide_score > best_score:
                    best_score = peptide_score
                    best_peptide = peptide
            elif peptide_mass(peptide, mass_dict) > target_mass:
                break

    return best_peptide


# Ввод спектрального вектора и протеома
spectral_vector = list(map(int, input("Введите спектральный вектор: ").split()))
proteome = input("Введите последовательность протеома: ")

# Выбор набора масс (стандартные или укороченные)
if len(spectral_vector) > 30:
    letters, masses = get_AA_letters_masses().keys(), get_AA_letters_masses().values()
else:
    letters, masses = get_XZ_letters_masses().keys(), get_XZ_letters_masses().values()

print("Лучший пептид:", best_protein(proteome, spectral_vector, letters, masses))
