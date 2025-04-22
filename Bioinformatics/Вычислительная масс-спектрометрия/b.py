def theoretical_spectrum(peptide):
    alphabet = ['A','C','D','E','F','G','H','I','K','L','M','N','P','Q','R','S','T','V','W','Y']
    masses = [71, 103, 115, 129, 147, 57, 137, 113, 128, 113, 131, 114, 97, 128, 156, 87, 101, 99, 186, 163]
    mass_dict = dict(zip(alphabet, masses))
    
    n = len(peptide)
    prefix_masses = [0]  
    current_mass = 0
    
    for amino_acid in peptide:
        current_mass += mass_dict[amino_acid]
        prefix_masses.append(current_mass)
    
    suffix_masses = []
    for i in range(n):
        suffix_mass = sum(mass_dict[peptide[j]] for j in range(i, n))
        suffix_masses.append(suffix_mass)

    total_masses = set(prefix_masses + suffix_masses)
    
    sorted_masses = sorted(total_masses)
    return sorted_masses

def find_peptide_from_spectrum(spectrum):
    alphabet = ['A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y']
    masses = [71, 103, 115, 129, 147, 57, 137, 113, 128, 113, 131, 114, 97, 128, 156, 87, 101, 99, 186, 163]
    mass_to_aa = {mass: aa for aa, mass in zip(alphabet, masses)}

    spectrum = list(map(int, spectrum.split()))
    n = len(spectrum)

    peptides = {0: ['']}

    for mass in spectrum[1:]:
        new_peptides = {}
        for prev_mass in list(peptides.keys()):
            if prev_mass < mass:
                diff = mass - prev_mass
                if diff in mass_to_aa:
                    for peptide in peptides[prev_mass]:
                        new_peptide = peptide + mass_to_aa[diff]
                        if mass not in new_peptides:
                            new_peptides[mass] = []
                        new_peptides[mass].append(new_peptide)
        peptides.update(new_peptides)

    if spectrum[-1] in peptides:
        for peptide in peptides[spectrum[-1]]:
            if theoretical_spectrum(peptide) == spectrum:
                return peptide
    return ''

sample_spectrum = input()
print(find_peptide_from_spectrum(sample_spectrum))
