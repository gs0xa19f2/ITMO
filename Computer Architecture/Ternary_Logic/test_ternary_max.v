`include "ternary_logic.v"

module test_ternary_max;
  reg [1:0] a, b;
  wire [1:0] out;

  ternary_max max1(a, b, out);

  initial begin
    $dumpfile("dump_max.vcd");
    $dumpvars(0, test_ternary_max);

    $monitor("Time: %0t | a = %b, b = %b => max = %b", $time, a, b, out);

    #5 a = 2'b00; b = 2'b00; // - vs -
    #5 a = 2'b00; b = 2'b01; // - vs 0
    #5 a = 2'b00; b = 2'b10; // - vs +
    #5 a = 2'b01; b = 2'b01; // 0 vs 0
    #5 a = 2'b01; b = 2'b10; // 0 vs +
    #5 a = 2'b10; b = 2'b10; // + vs +
    #5 a = 2'b10; b = 2'b01; // + vs 0
    #5 a = 2'b10; b = 2'b00; // + vs -
    #5 a = 2'b01; b = 2'b00; // 0 vs -
    #5 $finish;
  end
endmodule

