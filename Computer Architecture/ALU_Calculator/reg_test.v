`include "calculator.v"

module register_file_test();
    reg clk;
    reg [1:0] rd_addr;
    reg [1:0] we_addr;
    reg [3:0] we_data;
    wire [3:0] rd_data;

    register_file rf (
        .clk(clk),
        .rd_addr(rd_addr),
        .we_addr(we_addr),
        .we_data(we_data),
        .rd_data(rd_data)
    );

    initial begin
        clk = 0;
        we_addr = 2'b00;
        we_data = 4'b0000;
        rd_addr = 2'b00;
        #10;

        we_addr = 2'b00;
        we_data = 4'b1010; 
        #10; clk = 1; #10; clk = 0;

        rd_addr = 2'b00;
        #10;

        we_addr = 2'b01;
        we_data = 4'b0101;
        #10; clk = 1; #10; clk = 0;

        rd_addr = 2'b01;
        #10;

        we_addr = 2'b10;
        we_data = 4'b1111;
        #10; clk = 1; #10; clk = 0;

        rd_addr = 2'b10;
        #10;

        we_addr = 2'b11;
        we_data = 4'b0001; 
        #10; clk = 1; #10; clk = 0;

        rd_addr = 2'b11;
        #10;

        we_addr = 2'b00;
        we_data = 4'b0011; 
        #10; clk = 1; #10; clk = 0;

        rd_addr = 2'b00;
        #10;

        // Проверка чтения без записи
        we_addr = 2'b00;
        we_data = 4'b0000; // Не изменяем данные
        #10; clk = 1; #10; clk = 0;
        rd_addr = 2'b00;
        #10;

        $finish;
    end

    initial begin
        $monitor("Time=%0t | Write Addr=%b, Write Data=%b | Read Addr=%b, Read Data=%b", 
                 $time, we_addr, we_data, rd_addr, rd_data);
    end
endmodule

