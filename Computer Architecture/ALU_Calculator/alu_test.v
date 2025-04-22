`include "calculator.v"

module alu_test();
  reg [3:0] a, b;
  reg [2:0] control;
  wire [3:0] res;
  wire carry_out; 
  reg [47:0] control_str; 


  alu alu_inst(
      .a(a), 
      .b(b), 
      .control(control), 
      .res(res), 
      .carry_out(carry_out)  
  );


  initial begin

    control_str = control_string(control);
    $monitor("control = %b (%s), a = %b (%d), b = %b (%d), res = %b (%d), carry_out = %b", 
              control, control_str, a, a, b, b, res, res, carry_out);



    // AND
    #5 control = 3'b000; a = 4'b1100; b = 4'b1010; control_str = control_string(control);
    #5 control = 3'b000; a = 4'b0001; b = 4'b0011; control_str = control_string(control);
    #5 control = 3'b000; a = 4'b1111; b = 4'b0000; control_str = control_string(control);

    // OR
    #5 control = 3'b001; a = 4'b1100; b = 4'b1010; control_str = control_string(control);
    #5 control = 3'b001; a = 4'b0001; b = 4'b0011; control_str = control_string(control);
    #5 control = 3'b001; a = 4'b1111; b = 4'b0000; control_str = control_string(control);

    // ADD
    #5 control = 3'b010; a = 4'b0011; b = 4'b0101; control_str = control_string(control);
    #5 control = 3'b010; a = 4'b1111; b = 4'b0001; control_str = control_string(control);
    #5 control = 3'b010; a = 4'b1000; b = 4'b1000; control_str = control_string(control);

    // AND_NOT_B
    #5 control = 3'b100; a = 4'b1100; b = 4'b1010; control_str = control_string(control);
    #5 control = 3'b100; a = 4'b0011; b = 4'b0001; control_str = control_string(control);

    // OR_NOT_B
    #5 control = 3'b101; a = 4'b1100; b = 4'b1010; control_str = control_string(control);
    #5 control = 3'b101; a = 4'b0011; b = 4'b0001; control_str = control_string(control);

    // SUBTRACT
    #5 control = 3'b110; a = 4'b0101; b = 4'b0011; control_str = control_string(control);
    #5 control = 3'b110; a = 4'b1000; b = 4'b0011; control_str = control_string(control);
    #5 control = 3'b110; a = 4'b0001; b = 4'b0010; control_str = control_string(control);

    // SLT
    #5 control = 3'b111; a = 4'b0011; b = 4'b0110; control_str = control_string(control);
    #5 control = 3'b111; a = 4'b0110; b = 4'b0011; control_str = control_string(control);
    #5 control = 3'b111; a = 4'b1000; b = 4'b0111; control_str = control_string(control);

    // Краевые случаи
    // Сложение максимальных значений
    #5 control = 3'b010; a = 4'b1111; b = 4'b1111; control_str = control_string(control);
    // Вычитание с отрицательным результатом
    #5 control = 3'b110; a = 4'b0000; b = 4'b0001; control_str = control_string(control);
    // Сложение с нулевыми операндами
    #5 control = 3'b010; a = 4'b0000; b = 4'b0000; control_str = control_string(control);
    // Вычитание нулей
    #5 control = 3'b110; a = 4'b0000; b = 4'b0000; control_str = control_string(control);

    #5 $finish;
  end

  function [47:0] control_string(input [2:0] control);
    case (control)
      3'b000: control_string = "AND";
      3'b001: control_string = "OR";
      3'b010: control_string = "ADD";
      3'b100: control_string = "AND_NOT_B";
      3'b101: control_string = "OR_NOT_B";
      3'b110: control_string = "SUBTRACT";
      3'b111: control_string = "SLT";
      default: control_string = "UNKNOWN";
    endcase
  endfunction
endmodule

