// Определение базовых логических вентилей на транзисторном уровне

// Модуль NOT-ворота
module not_gate(in, out);
  input in;
  output out;
  
  supply1 vdd; // Питание
  supply0 gnd; // Земля
  
  pmos p1(out, vdd, in);
  nmos n1(out, gnd, in);
endmodule

// Модуль NAND-ворота
module nand_gate(in1, in2, out);
  input in1, in2;
  output out;
  
  supply1 vdd;
  supply0 gnd;
  
  wire n1_out;
  
  pmos p1(out, vdd, in1);
  pmos p2(out, vdd, in2);
  nmos n1(n1_out, gnd, in1);
  nmos n2(out, n1_out, in2);
endmodule

// Модуль AND-ворота, реализованный через NAND и NOT
module and_gate(in1, in2, out);
  input in1, in2;
  output out;
  
  wire nand_out;
  
  nand_gate nand1(in1, in2, nand_out);
  not_gate not1(nand_out, out);
endmodule

// Модуль OR-ворота, реализованный через NAND и NOT
module or_gate(in1, in2, out);
  input in1, in2;
  output out;
  
  wire not_in1, not_in2;
  wire nand_out;
  
  not_gate not1(in1, not_in1);
  not_gate not2(in2, not_in2);
  
  nand_gate nand1(not_in1, not_in2, nand_out);
  not_gate not3(nand_out, out);
endmodule

// Модуль XOR-ворота, реализованный через базовые вентилы
module xor_gate(in1, in2, out);
  input in1, in2;
  output out;
  
  wire a, b, c;
  
  not_gate not_a(in1, a);
  not_gate not_b(in2, b);
  
  and_gate and1(a, in2, c);
  and_gate and2(in1, b, out);
  
  or_gate or1(c, out, out);
endmodule

// Модуль мультиплексора для 1-битных сигналов
module mux2_1bit(sel, in0, in1, out);
  input sel;
  input in0, in1;
  output out;
  
  wire nand_notsel_in0, nand_sel_in1, and_out;
  
  // Генерация NAND(~sel, in0)
  nand_gate nand0(~sel, in0, nand_notsel_in0);
  
  // Генерация NAND(sel, in1)
  nand_gate nand1(sel, in1, nand_sel_in1);
  
  // Генерация AND(NAND(~sel, in0), NAND(sel, in1))
  and_gate and1(nand_notsel_in0, nand_sel_in1, and_out);
  
  // Генерация NOT(AND(...)) для получения конечного выхода
  not_gate not1(and_out, out);
endmodule

// Модуль мультиплексора для 2-битных сигналов
module mux2_2bit(sel, in0, in1, out);
  input sel;
  input [1:0] in0, in1;
  output [1:0] out;
  
  wire out0, out1;
  
  mux2_1bit mux0(sel, in0[0], in1[0], out0);
  mux2_1bit mux1(sel, in0[1], in1[1], out1);
  
  assign out = {out1, out0};
endmodule

// Модуль декодера троичного трита
module ternary_decoder(trit, is_neg, is_zero, is_pos);
  input [1:0] trit;
  output is_neg, is_zero, is_pos;

  wire not_trit0, not_trit1;

  not_gate not0(trit[0], not_trit0);
  not_gate not1(trit[1], not_trit1);

  // is_neg: (trit == 00)
  and_gate and_neg(not_trit1, not_trit0, is_neg);

  // is_zero: (trit == 01)
  and_gate and_zero(not_trit1, trit[0], is_zero);

  // is_pos: (trit == 10)
  and_gate and_pos(trit[1], not_trit0, is_pos);
endmodule

// Модуль MIN для троичной логики
module ternary_min(a, b, out);
  input [1:0] a, b;
  output [1:0] out;
  
  wire a_neg, a_zero, a_pos;
  wire b_neg, b_zero, b_pos;
  
  ternary_decoder decoder_a(a, a_neg, a_zero, a_pos);
  ternary_decoder decoder_b(b, b_neg, b_zero, b_pos);

  wire [1:0] min_select;

  assign min_select = (a_neg | b_neg) ? (a_neg ? a : b) : 
                      (a_zero | b_zero) ? (a_zero ? a : b) : 
                      a; 

  assign out = min_select;
endmodule


// Модуль MAX для троичной логики
module ternary_max(a, b, out);
  input [1:0] a, b;
  output [1:0] out;
  
  wire a_neg, a_zero, a_pos;
  wire b_neg, b_zero, b_pos;
  
  ternary_decoder decoder_a(a, a_neg, a_zero, a_pos);
  ternary_decoder decoder_b(b, b_neg, b_zero, b_pos);
  
  wire [1:0] max_select;

  assign max_select = (a_pos | b_pos) ? (a_pos ? a : b) : 
                      (a_zero | b_zero) ? (a_zero ? a : b) : 
                      a; 

  assign out = max_select;
endmodule

// Модуль CONSENSUS для троичной логики
module ternary_consensus(a, b, out);
  input [1:0] a, b;
  output [1:0] out;
  
  wire a_neg, a_zero, a_pos;
  wire b_neg, b_zero, b_pos;
  
  ternary_decoder decoder_a(a, a_neg, a_zero, a_pos);
  ternary_decoder decoder_b(b, b_neg, b_zero, b_pos);
  
  // Определяем, равны ли триты a и b для всех случаев: отрицательное, нулевое, положительное
  wire a_eq_b_neg, a_eq_b_zero, a_eq_b_pos;
  and_gate and_eq1(a_neg, b_neg, a_eq_b_neg);     
  and_gate and_eq2(a_zero, b_zero, a_eq_b_zero);  
  and_gate and_eq3(a_pos, b_pos, a_eq_b_pos);     
  
  wire a_b_both_neg;
  and_gate and_both_neg(a_eq_b_neg, a_eq_b_neg, a_b_both_neg);
  
  wire a_b_both_pos;
  and_gate and_both_pos(a_eq_b_pos, a_eq_b_pos, a_b_both_pos);
  
  assign out = a_b_both_neg ? 2'b00 :       // Оба трита 00, вернуть 00
               a_b_both_pos ? 2'b10 :       // Оба трита 10, вернуть 10
               2'b01;                       // В любых других случаях вернуть 01
endmodule


// Модуль ANY для троичной логики
module ternary_any(a, b, out);
  input [1:0] a, b;
  output [1:0] out;
  
  wire a_neg, a_zero, a_pos;
  wire b_neg, b_zero, b_pos;
  
  ternary_decoder decoder_a(a, a_neg, a_zero, a_pos);
  ternary_decoder decoder_b(b, b_neg, b_zero, b_pos);
  
  // Логика ANY:
  // Если оба трита положительные, то '+', то есть '10'.
  // Если один трит положительный, а другой отрицательный, то '01'.
  // Если один трит нулевой, а другой положительный, то '10'.
  // Если один трит нулевой, а другой отрицательный, то '00'.
  // Если оба трита нулевые, то '01'.
  // Если оба трита отрицательные, то '-'.

  wire both_pos;
  and_gate and_both_pos(a_pos, b_pos, both_pos); 

  wire pos_and_neg;
  and_gate and_pos_neg(a_pos, b_neg, pos_and_neg);
  wire neg_and_pos;
  and_gate and_neg_pos(b_pos, a_neg, neg_and_pos); 

  wire any_zero_pos;
  and_gate and_zero_pos_a(a_zero, b_pos, any_zero_pos); 
  wire any_zero_pos_b;
  and_gate and_zero_pos_b(b_zero, a_pos, any_zero_pos_b);

  wire any_zero_neg;
  and_gate and_zero_neg_a(a_zero, b_neg, any_zero_neg); 
  wire any_zero_neg_b;
  and_gate and_zero_neg_b(b_zero, a_neg, any_zero_neg_b); 

  wire both_zero;
  and_gate and_both_zero(a_zero, b_zero, both_zero);

  wire both_neg;
  and_gate and_both_neg(a_neg, b_neg, both_neg);

  assign out = both_pos ? 2'b10 :                 
               (pos_and_neg | neg_and_pos) ? 2'b01 : 
               (any_zero_pos | any_zero_pos_b) ? 2'b10 :  
               (any_zero_neg | any_zero_neg_b) ? 2'b00 :  
               both_zero ? 2'b01 :             
               both_neg ? 2'b00 : 2'b00;        
endmodule

