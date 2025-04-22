`include "calculator.v"

module calculator_test();
    reg clk;
    reg [1:0] rd_addr;
    reg [1:0] we_addr;
    reg [2:0] control;
    reg [3:0] immediate;
    wire [3:0] rd_data;

    calculator calc (
        .clk(clk),
        .rd_addr(rd_addr),
        .immediate(immediate),
        .we_addr(we_addr),
        .control(control),
        .rd_data(rd_data)
    );

    initial begin
        clk = 0;
        we_addr = 2'b00;
        immediate = 4'b0000;
        control = 3'b000; 
        rd_addr = 2'b00;
        #10;

        forever #5 clk = ~clk;
    end

    initial begin
        #10;
        // Операция AND: r0 = r0 & 1010
        control = 3'b000;
        immediate = 4'b1010;
        we_addr = 2'b00;
        rd_addr = 2'b00;
        #10;

        // Операция OR: r0 = r0 | 0101
        control = 3'b001;
        immediate = 4'b0101;
        we_addr = 2'b00;
        rd_addr = 2'b00;
        #10;

        // Операция ADD: r0 = r0 + 0011
        control = 3'b010;
        immediate = 4'b0011;
        we_addr = 2'b00;
        rd_addr = 2'b00;
        #10;

        // Операция AND_NOT_B: r1 = r0 & !0101
        control = 3'b100;
        immediate = 4'b0101;
        we_addr = 2'b01;
        rd_addr = 2'b01;
        #10;

        // Операция OR_NOT_B: r2 = r0 | !0011
        control = 3'b101;
        immediate = 4'b0011;
        we_addr = 2'b10;
        rd_addr = 2'b10;
        #10;

        // Операция SUBTRACT: r3 = r0 - 0010
        control = 3'b110;
        immediate = 4'b0010;
        we_addr = 2'b11;
        rd_addr = 2'b11;
        #10;

        // Операция SLT: r0 < r1 ?
        control = 3'b111;
        immediate = 4'b0000; // Значение не важно для SLT
        we_addr = 2'b00;
        rd_addr = 2'b00;
        #10;

        // Дополнительные операции и краевые случаи
        // Например, проверка переполнения при ADD
        control = 3'b010;
        immediate = 4'b1111;
        we_addr = 2'b00;
        rd_addr = 2'b00;
        #10;

        // Проверка SLT, когда a >= b
        control = 3'b111;
        immediate = 4'b1111;
        we_addr = 2'b00;
        rd_addr = 2'b00;
        #10;

        // Завершение симуляции
        $finish;
    end

    initial begin
        $monitor("Time=%0t | Control=%b | Immediate=%b | Write Addr=%b | Read Addr=%b | Read Data=%b", 
                 $time, control, immediate, we_addr, rd_addr, rd_data);
    end
endmodule

