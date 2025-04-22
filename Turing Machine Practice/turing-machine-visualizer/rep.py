def find_repeated_entries(program_text):
    entries = {} 
    repeated_entries = set()  

    for line in program_text.strip().split('\n'):
        if '->' in line:
            left_part = line.split('->')[0].strip() 
            if left_part in entries:
                repeated_entries.add(left_part)  
            entries[left_part] = entries.get(left_part, 0) + 1  

    for entry in sorted(repeated_entries): 
        print(f"{entry} -> {entries[entry]} раз")
        
program_text = """
3

S 1 _ _ -> S' _ > 1 > _ ^
S 0 _ _ -> S' _ > 0 > _ ^
S | _ _ -> S _ > _ ^ _ ^

S' 1 _ _ -> S' _ > 1 > _ ^
S' 0 _ _ -> S' _ > 0 > _ ^
S' | _ _ -> checkDeli _ > | > _ ^
S' _ _ _ -> toStart _ ^ _ < _ ^

checkDeli 1 _ _ -> S' _ > 1 > _ ^
checkDeli 0 _ _ -> S' _ > 0 > _ ^
checkDeli | _ _ -> deliCopy' _ > _ ^ _ ^

deliCopy 1 _ _ -> deliCopy _ > 1 > _ ^
deliCopy 0 _ _ -> deliCopy _ > 0 > _ ^
deliCopy | _ _ -> deliCopy' _ > | > _ ^
deliCopy _ _ _ -> checkLastDeli _ ^ _ < _ ^

checkLastDeli _ | _ -> checkLastDeli _ ^ _ < _ ^
checkLastDeli _ 0 _ -> toStart _ ^ 0 ^ _ ^
checkLastDeli _ 1 _ -> toStart _ ^ 1 ^ _ ^

deliCopy' 1 _ _ -> deliCopy _ > 1 > _ ^
deliCopy' 0 _ _ -> deliCopy _ > 0 > _ ^
deliCopy' | _ _ -> deliCopy' _ > _ ^ _ ^
deliCopy' _ _ _ -> checkLastDeli _ ^ _ < _ ^

toStart _ 1 _ -> toStart 1 < _ < _ ^
toStart _ 0 _ -> toStart 0 < _ < _ ^
toStart _ | _ -> toStart | < _ < _ ^
toStart _ _ _ -> start _ > _ ^ _ ^

start 1 _ _ -> start 1 > 1 > _ ^
start 0 _ _ -> start 0 > 0 > _ ^
start | _ _ -> copyBelow | > | ^ _ ^
start _ _ _ -> finalize _ < _ ^ _ ^

copyBelow 1 | _ -> copyBelow 1 > | ^ 1 >
copyBelow 0 | _ -> copyBelow 0 > | ^ 0 >
copyBelow 1 _ _ -> copyBelow 1 ^ | ^ _ ^
copyBelow 0 _ _ -> compare 0 > _ < 0 ^
copyBelow | | _ -> compare | ^ | < _ <
copyBelow _ | _ -> compare L ^ | < _ <
copyBelow _ _ _ -> finalize _ < _ ^ _ ^

compare _ 1 0 -> compare L ^ 1 ^ 0 ^
compare | 0 0 -> compare | ^ X < X <
compare | 0 1 -> compare | ^ X < Y <
compare | 1 0 -> compare | ^ Y < X <
compare | 1 1 -> compare | ^ Y < Y <
compare | _ 1 -> placeRight | ^ _ > 1 <
compare | | 1 -> placingRight | ^ | > 1 >
compare | | 0 -> placingRight | ^ | > 0 >
compare | _ 0 -> placeRight | ^ _ > 0 <
compare | 1 _ -> compareNext | ^ 1 ^ _ >
compare | 0 _ -> compareNext | ^ 0 ^ _ >
compare | _ _ -> evenCompare | ^ _ > _ >
compare | | _ -> evenCompare | ^ | > _ >
compare L 0 0 -> compare L ^ X < X <
compare L 0 1 -> compare L ^ X < Y <
compare L 1 0 -> compare L ^ Y < X <
compare L 1 1 -> compare L ^ Y < Y <
compare L _ 1 -> placeRight L ^ _ > 1 <
compare L | 1 -> placingRight L ^ | > 1 >
compare L | 0 -> placingRight L ^ | > 0 >
compare L _ 0 -> placeRight L ^ _ > 0 <
compare L 1 _ -> compareNext L ^ 1 ^ _ >
compare L 0 _ -> compareNext L ^ 0 ^ _ >
compare L _ _ -> evenCompare L ^ _ > _ >
compare L | _ -> evenCompare L ^ | > _ >

evenCompare | Y Y -> evenCompare | ^ 1 > 1 >
evenCompare | X X -> evenCompare | ^ 0 > 0 >
evenCompare | Y X -> compareNext' | ^ 1 > 0 >
evenCompare | X Y -> placeRight' | ^ 0 > 1 <
evenCompare | | _ -> placeRight' | ^ | ^ _ <
evenCompare L Y Y -> evenCompare L ^ 1 > 1 >
evenCompare L X X -> evenCompare L ^ 0 > 0 >
evenCompare L Y X -> compareNext' L ^ 1 > 0 >
evenCompare L X Y -> placeRight' L ^ 0 > 1 <
evenCompare L | _ -> placeRight' L ^ | ^ _ <

placeRight' | 0 0 -> placeRight' | ^ 0 > 0 <
placeRight' | 0 1 -> placeRight' | ^ 0 > 1 <
placeRight' | 1 0 -> placeRight' | ^ 1 > 0 <
placeRight' | 1 1 -> placeRight' | ^ 1 > 1 <
placeRight' | | 0 -> placeRight' | ^ | ^ 0 <
placeRight' | | 1 -> placeRight' | ^ | ^ 1 <
placeRight' | Y 1 -> placeRight' | ^ 1 > 1 <
placeRight' | Y 0 -> placeRight' | ^ 1 > 0 <
placeRight' | X 0 -> placeRight' | ^ 0 > 0 <
placeRight' | X 1 -> placeRight' | ^ 0 > 1 <
placeRight' | | _ -> placingRight | ^ | > _ >
placeRight' | X _ -> placingRight | ^ 0 > _ >
placeRight' | Y _ -> placingRight | ^ 1 > _ >
placeRight' L 0 0 -> placeRight' L ^ 0 > 0 <
placeRight' L 0 1 -> placeRight' L ^ 0 > 1 <
placeRight' L 1 0 -> placeRight' L ^ 1 > 0 <
placeRight' L 1 1 -> placeRight' L ^ 1 > 1 <
placeRight' L | 0 -> placeRight' L ^ | ^ 0 <
placeRight' L | 1 -> placeRight' L ^ | ^ 1 <
placeRight' L | _ -> placingRight L ^ | > _ >
placeRight' L Y 1 -> placeRight' L ^ 1 > 1 <
placeRight' L Y 0 -> placeRight' L ^ 1 > 0 <
placeRight' L X 0 -> placeRight' L ^ 0 > 0 <
placeRight' L X 1 -> placeRight' L ^ 0 > 1 <
placeRight' L X _ -> placingRight L ^ 0 > _ >
placeRight' L Y _ -> placingRight L ^ 1 > _ >

compareNext' | X Y -> compareNext' | ^ 0 > 1 >
compareNext' | Y X -> compareNext' | ^ 1 > 0 >
compareNext' | X X -> compareNext' | ^ 0 > 0 >
compareNext' | Y Y -> compareNext' | ^ 1 > 1 >
compareNext' | | _ -> compareNext' | ^ | < _ <
compareNext' | 0 0 -> compareNext' | ^ 0 < 0 ^
compareNext' | 0 1 -> compareNext' | ^ 0 < 1 ^
compareNext' | 1 0 -> compareNext' | ^ 1 < 0 ^
compareNext' | 1 1 -> compareNext' | ^ 1 < 1 ^
compareNext' | | 0 -> compareNext | ^ | < 0 ^ 
compareNext' | | 1 -> compareNext | ^ | < 1 ^
compareNext' | _ 0 -> placingLeft | ^ | < 0 ^
compareNext' | _ 1 -> placingLeft | ^ | < 1 ^
compareNext' L X Y -> compareNext' L ^ 0 > 1 >
compareNext' L Y X -> compareNext' L ^ 1 > 0 >
compareNext' L X X -> compareNext' L ^ 0 > 0 >
compareNext' L Y Y -> compareNext' L ^ 1 > 1 >
compareNext' L | _ -> compareNext' L ^ | < _ <
compareNext' L 0 0 -> compareNext' L ^ 0 < 0 ^
compareNext' L 0 1 -> compareNext' L ^ 0 < 1 ^
compareNext' L 1 0 -> compareNext' L ^ 1 < 0 ^
compareNext' L 1 1 -> compareNext' L ^ 1 < 1 ^
compareNext' L | 0 -> compareNext L ^ | < 0 ^ 
compareNext' L | 1 -> compareNext L ^ | < 1 ^
compareNext' L _ 0 -> placingLeft L ^ | < 0 ^
compareNext' L _ 1 -> placingLeft L ^ | < 1 ^

placingLeft | _ 0 -> placingLeft | ^ 0 < _ < 
placingLeft | _ 1 -> placingLeft | ^ 1 < _ <
placingLeft | _ _ -> normalize | ^ _ > _ ^
placingLeft L _ 0 -> placingLeft L ^ 0 < _ < 
placingLeft L _ 1 -> placingLeft L ^ 1 < _ <
placingLeft L _ _ -> normalize L ^ _ > _ ^

placingLeft' L 0 0 -> placingLeft' L ^ 0 < 0 ^
placingLeft' L 0 1 -> placingLeft' L ^ 0 < 1 ^
placingLeft' L 1 0 -> placingLeft' L ^ 1 < 0 ^
placingLeft' L 1 1 -> placingLeft' L ^ 1 < 1 ^
placingLeft' L _ 0 -> placingLeft L ^ | < 0 ^
placingLeft' L _ 1 -> placingLeft L ^ | < 1 ^

normalize | 1 _ -> normalize | ^ 1 > _ ^
normalize | 0 _ -> normalize | ^ 0 > _ ^
normalize | | _ -> normalize | ^ | > _ ^
normalize | X _ -> normalize | ^ 0 > _ ^
normalize | Y _ -> normalize | ^ 1 > _ ^
normalize | _ _ -> copyBelow' | ^ _ < _ ^
normalize L 1 _ -> normalize L ^ 1 > _ ^
normalize L 0 _ -> normalize L ^ 0 > _ ^
normalize L | _ -> normalize L ^ | > _ ^
normalize L X _ -> normalize L ^ 0 > _ ^
normalize L Y _ -> normalize L ^ 1 > _ ^
normalize L _ _ -> copyBelow' L ^ _ < _ ^

copyBelow' | | _ -> copyBelow | > | ^ _ ^
copyBelow' | 0 _ -> copyBelow | > 0 > _ ^
copyBelow' | 1 _ -> copyBelow | > 1 > _ ^
copyBelow' L | _ -> prepareFinalize L ^ _ < _ ^

compareNext | 0 X -> compareNext | ^ 0 < 0 >
compareNext | 0 Y -> compareNext | ^ 0 < 1 >
compareNext | 1 X -> compareNext | ^ 1 < 0 >
compareNext | 1 Y -> compareNext | ^ 1 < 1 >
compareNext | | X -> compareNext | ^ | ^ 0 >
compareNext | | Y -> compareNext | ^ | ^ 1 >
compareNext | | 0 -> compareNext | ^ | ^ 0 >
compareNext | | 1 -> compareNext | ^ | ^ 1 >
compareNext | 0 _ -> compareNext | ^ 0 < _ ^
compareNext | 1 _ -> compareNext | ^ 1 < _ ^
compareNext | _ X -> compareNext | ^ _ ^ 0 >
compareNext | _ Y -> compareNext | ^ _ ^ 1 >
compareNext | _ _ -> compareNext' | ^ _ ^ _ <
compareNext | | _ -> compare | ^ | < _ < 
compareNext | 1 1 -> compare | ^ 1 ^ 1 ^
compareNext | 0 0 -> compare | ^ 0 ^ 0 ^
compareNext | 1 0 -> compare | ^ 1 ^ 0 ^ 
compareNext | 0 1 -> compare | ^ 0 ^ 1 ^
compareNext L 0 X -> compareNext L ^ 0 < 0 >
compareNext L 0 Y -> compareNext L ^ 0 < 1 >
compareNext L 1 X -> compareNext L ^ 1 < 0 >
compareNext L 1 Y -> compareNext L ^ 1 < 1 >
compareNext L | X -> compareNext L ^ | ^ 0 >
compareNext L | Y -> compareNext L ^ | ^ 1 >
compareNext L | 0 -> compareNext L ^ | ^ 0 >
compareNext L | 1 -> compareNext L ^ | ^ 1 >
compareNext L 0 _ -> compareNext L ^ 0 < _ ^
compareNext L 1 _ -> compareNext L ^ 1 < _ ^
compareNext L _ X -> compareNext L ^ _ ^ 0 >
compareNext L _ Y -> compareNext L ^ _ ^ 1 >
compareNext L | _ -> compare L ^ | < _ < 
compareNext L 1 1 -> compare L ^ 1 ^ 1 ^
compareNext L 0 0 -> compare L ^ 0 ^ 0 ^
compareNext L 1 0 -> compare L ^ 1 ^ 0 ^ 
compareNext L 0 1 -> compare L ^ 0 ^ 1 ^
compareNext L _ _ -> prepareFinalize L > _ > _ ^

prepareFinalize L 1 _ -> prepareFinalize L > 1 > _ ^
prepareFinalize L 0 _ -> prepareFinalize L > 0 > _ ^
prepareFinalize L | _ -> prepareFinalize L > _ ^ _ ^
prepareFinalize L _ _ -> prepareFinalize L > _ ^ _ ^
prepareFinalize _ 1 _ -> prepareFinalize _ ^ 1 > _ ^
prepareFinalize _ 0 _ -> prepareFinalize _ ^ 0 > _ ^
prepareFinalize _ | _ -> prepareFinalize _ ^ | > _ ^
prepareFinalize _ _ _ -> finalize' _ < _ < _ ^
prepareFinalize _ Y _ -> placingLeft' _ < 1 < _ <
prepareFinalize _ X _ -> placingLeft' _ < 0 < _ <

placeRight | _ 1 -> placeRight | ^ _ > 1 <
placeRight | _ 0 -> placeRight | ^ _ > 0 <
placeRight | X 0 -> placeRight | ^ 0 > 0 <
placeRight | X 1 -> placeRight | ^ 0 > 1 <
placeRight | Y 0 -> placeRight | ^ 1 > 0 <
placeRight | Y 1 -> placeRight | ^ 1 > 1 <
placeRight | X _ -> placeRight | ^ 0 > _ ^
placeRight | Y _ -> placeRight | ^ 1 > _ ^
placeRight | X Y -> placeRight | ^ 0 > 1 >
placeRight | X X -> placeRight | ^ 0 > 0 >
placeRight | Y X -> placeRight | ^ 1 > 0 >
placeRight | Y Y -> placeRight | ^ 1 > 1 >
placeRight | | 0 -> placeRight | ^ | ^ 0 <
placeRight | | 1 -> placeRight | ^ | ^ 1 <
placeRight | | _ -> placingRight | ^ | > _ >
placeRight L _ 1 -> placeRight L ^ _ > 1 <
placeRight L _ 0 -> placeRight L ^ _ > 0 <
placeRight L X 0 -> placeRight L ^ 0 > 0 <
placeRight L X 1 -> placeRight L ^ 0 > 1 <
placeRight L Y 0 -> placeRight L ^ 1 > 0 <
placeRight L Y 1 -> placeRight L ^ 1 > 1 <
placeRight L X _ -> placeRight L ^ 0 > _ ^
placeRight L Y _ -> placeRight L ^ 1 > _ ^
placeRight L X Y -> placeRight L ^ 0 > 1 >
placeRight L X X -> placeRight L ^ 0 > 0 >
placeRight L Y X -> placeRight L ^ 1 > 0 >
placeRight L Y Y -> placeRight L ^ 1 > 1 >
placeRight L | 0 -> placeRight L ^ | ^ 0 <
placeRight L | 1 -> placeRight L ^ | ^ 1 <
placeRight L | _ -> placingRight L ^ | > _ >

placingRight | _ 1 -> placingRight | ^ 1 > _ > 
placingRight | _ Y -> placingRight | ^ 1 > _ >
placingRight | _ 0 -> placingRight | ^ 0 > _ >
placingRight | _ X -> placingRight | ^ 0 > _ >
placingRight | 1 1 -> placingRight | ^ 1 ^ 1 >
placingRight | 1 0 -> placingRight | ^ 1 ^ 0 >
placingRight | 0 1 -> placingRight | ^ 0 ^ 1 >
placingRight | 0 0 -> placingRight | ^ 0 ^ 0 >
placingRight | 1 X -> placingRight | ^ 1 ^ 0 >
placingRight | 1 Y -> placingRight | ^ 1 ^ 1 >
placingRight | 0 X -> placingRight | ^ 0 ^ 0 >
placingRight | 0 Y -> placingRight | ^ 0 ^ 1 >
placingRight | Y Y -> placingRight | ^ 1 > 1 <
placingRight | X X -> placingRight | ^ 0 > 0 <
placingRight | X Y -> placingRight | ^ 0 > 1 <
placingRight | Y X -> placingRight | ^ 1 > 0 <
placingRight | Y 0 -> placingRight | ^ 1 > 0 <
placingRight | Y 1 -> placingRight | ^ 1 > 1 <
placingRight | X 0 -> placingRight | ^ 0 > 0 <
placingRight | X 1 -> placingRight | ^ 0 > 1 <
placingRight | | _ -> slidingRight | ^ | > _ >
placingRight | | 0 -> placingRight | ^ | ^ 0 <
placingRight | | 1 -> placingRight | ^ | ^ 1 <
placingRight | 1 _ -> copyingRight | ^ 1 ^ _ <
placingRight | _ _ -> copyBelow | > | ^ _ ^
placingRight | X _ -> placingRight | ^ 0 > _ ^
placingRight | Y _ -> placingRight | ^ 1 > _ ^
placingRight L Y Y -> placingRight L ^ 1 > 1 <
placingRight L X X -> placingRight L ^ 0 > 0 <
placingRight L X Y -> placingRight L ^ 0 > 1 <
placingRight L Y X -> placingRight L ^ 1 > 0 <
placingRight L Y 0 -> placingRight L ^ 1 > 0 <
placingRight L Y 1 -> placingRight L ^ 1 > 1 <
placingRight L X 0 -> placingRight L ^ 0 > 0 <
placingRight L X 1 -> placingRight L ^ 0 > 1 <
placingRight L | _ -> slidingRight L ^ | > _ >
placingRight L | 0 -> placingRight L ^ | ^ 0 <
placingRight L | 1 -> placingRight L ^ | ^ 1 <
placingRight L _ 1 -> placingRight L ^ 1 > _ > 
placingRight L _ Y -> placingRight L ^ 1 > _ >
placingRight L _ 0 -> placingRight L ^ 0 > _ >
placingRight L _ X -> placingRight L ^ 0 > _ >
placingRight L 1 1 -> placingRight L ^ 1 ^ 1 >
placingRight L 1 0 -> placingRight L ^ 1 ^ 0 >
placingRight L 0 1 -> placingRight L ^ 0 ^ 1 >
placingRight L 0 0 -> placingRight L ^ 0 ^ 0 >
placingRight L 1 X -> placingRight L ^ 1 ^ 0 >
placingRight L 1 Y -> placingRight L ^ 1 ^ 1 >
placingRight L 0 X -> placingRight L ^ 0 ^ 0 >
placingRight L 0 Y -> placingRight L ^ 0 ^ 1 >
placingRight L 1 _ -> copyingRight L ^ 1 ^ _ <
placingRight L _ _ -> copyBelow L > | ^ _ ^
placingRight L X _ -> placingRight L ^ 0 > _ ^
placingRight L Y _ -> placingRight L ^ 1 > _ ^

copyingRight | 1 1 -> copyingRight | ^ 1 ^ 1 >
copyingRight | 1 0 -> copyingRight | ^ 1 ^ 0 >
copyingRight | 0 1 -> copyingRight | ^ 0 ^ 1 >
copyingRight | 0 0 -> copyingRight | ^ 0 ^ 0 >
copyingRight | 0 X -> copyingRight | ^ 0 ^ 0 >
copyingRight | 0 Y -> copyingRight | ^ 0 ^ 1 >
copyingRight | 1 X -> copyingRight | ^ 1 ^ 0 >
copyingRight | 1 Y -> copyingRight | ^ 1 ^ 1 >
copyingRight | 1 _ -> copyingRight' | ^ 1 ^ | >
copyingRight | 0 _ -> copyingRight' | ^ 0 ^ | >
copyingRight L 1 1 -> copyingRight L ^ 1 ^ 1 >
copyingRight L 1 0 -> copyingRight L ^ 1 ^ 0 >
copyingRight L 0 1 -> copyingRight L ^ 0 ^ 1 >
copyingRight L 0 0 -> copyingRight L ^ 0 ^ 0 >
copyingRight L 1 _ -> copyingRight' L ^ 1 ^ | >
copyingRight L 0 _ -> copyingRight' L ^ 0 ^ | >
copyingRight L 0 X -> copyingRight L ^ 0 ^ 0 >
copyingRight L 0 Y -> copyingRight L ^ 0 ^ 1 >
copyingRight L 1 X -> copyingRight L ^ 1 ^ 0 >
copyingRight L 1 Y -> copyingRight L ^ 1 ^ 1 >

copyingRight' | 1 _ -> copyingRight' | ^ * > 1 >
copyingRight' | 0 _ -> copyingRight' | ^ * > 0 >
copyingRight' | X _ -> copyingRight' | ^ * > 0 >
copyingRight' | Y _ -> copyingRight' | ^ * > 1 >
copyingRight' | _ _ -> prepareCopR' | ^ _ < _ <
copyingRight' | | _ -> copyingRight' | ^ * > | >
copyingRight' L 1 _ -> copyingRight' L ^ * > 1 >
copyingRight' L 0 _ -> copyingRight' L ^ * > 0 >
copyingRight' L X _ -> copyingRight' L ^ * > 0 >
copyingRight' L Y _ -> copyingRight' L ^ * > 1 >
copyingRight' L | _ -> copyingRight' L ^ * > | >
copyingRight' L _ _ -> prepareCopR' L ^ _ < _ <

prepareCopR | * 0 -> prepareCopR | ^ _ < 0 <
prepareCopR | * 1 -> prepareCopR | ^ _ < 1 <
prepareCopR | * | -> prepareCopR | ^ _ < | <
prepareCopR | | | -> prepareCopR | ^ | ^ | <
prepareCopR | | 0 -> prepareCopR | ^ | ^ 0 <
prepareCopR | | 1 -> prepareCopR | ^ | ^ 1 <
prepareCopR | | _ -> slidingRight | ^ | > _ >
prepareCopR L * 0 -> prepareCopR L ^ _ < 0 <
prepareCopR L * 1 -> prepareCopR L ^ _ < 1 <
prepareCopR L * | -> prepareCopR L ^ _ < | <
prepareCopR L | | -> prepareCopR L ^ | ^ | <
prepareCopR L | 0 -> prepareCopR L ^ | ^ 0 <
prepareCopR L | 1 -> prepareCopR L ^ | ^ 1 <
prepareCopR L | _ -> slidingRight L ^ | > _ >

prepareCopR' | * | -> prepareCopR | ^ _ < _ <
prepareCopR' | * 1 -> prepareCopR | ^ _ < 1 <
prepareCopR' | * 0 -> prepareCopR | ^ _ < 0 <
prepareCopR' L * | -> prepareCopR L ^ _ < _ <
prepareCopR' L * 1 -> prepareCopR L ^ _ < 1 <
prepareCopR' L * 0 -> prepareCopR L ^ _ < 0 <

slidingRight | 1 1 -> copyingRight | ^ 1 ^ 1 >
slidingRight | 1 0 -> copyingRight | ^ 1 ^ 0 >
slidingRight | 0 1 -> copyingRight | ^ 0 ^ 1 >
slidingRight | _ 1 -> slidingRight | ^ 1 > _ >
slidingRight | _ 0 -> slidingRight | ^ 0 > _ >
slidingRight | _ | -> slidingRight | ^ | > _ >
slidingRight | _ _ -> copyBelow | > _ ^ _ ^
slidingRight | _ Y -> slidingRight | ^ 1 > _ >
slidingRight | _ X -> slidingRight | ^ 0 > _ >
slidingRight L _ 1 -> slidingRight L ^ 1 > _ >
slidingRight L _ 0 -> slidingRight L ^ 0 > _ >
slidingRight L _ | -> slidingRight L ^ | > _ >
slidingRight L _ _ -> copyBelow L > _ ^ _ ^
slidingRight L _ Y -> slidingRight L ^ 1 > _ >
slidingRight L _ X -> slidingRight L ^ 0 > _ >
slidingRight L 1 0 -> copyingRight L ^ 1 ^ 0 >
slidingRight L 0 1 -> copyingRight L ^ 0 ^ 1 >
slidingRight L 1 1 -> copyingRight L ^ 1 ^ 1 >

finalize 1 _ _ -> finalize _ < _ ^ _ ^
finalize 0 _ _ -> finalize _ < _ ^ _ ^
finalize | _ _ -> finalize _ < _ ^ _ ^
finalize L _ _ -> finalize _ < _ ^ _ ^
finalize _ _ _ -> fin _ ^ _ < _ ^

finalize' L | _ -> finalize L ^ _ ^ _ ^
finalize' L 1 _ -> finalize L ^ 1 > _ ^
finalize' L 0 _ -> finalize L ^ 0 > _ ^

fin _ 0 _ -> fin 0 < _ < _ ^
fin _ 1 _ -> fin 1 < _ < _ ^
fin _ | _ -> fin | < _ < _ ^
fin _ _ _ -> AC _ > _ ^ _ ^
"""

find_repeated_entries(program_text)
