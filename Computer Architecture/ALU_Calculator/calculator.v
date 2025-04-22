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

// Модуль OR-ворота, реализованный через AND и NOT
module or_gate(in1, in2, out);
  input in1, in2;
  output out;

  wire not_in1, not_in2;
  wire and_out;
  
  // Инвертируем входы
  not_gate not1(in1, not_in1);
  not_gate not2(in2, not_in2);
  
  // Используем прямое И-НЕ для OR
  and_gate and1(not_in1, not_in2, and_out);
  not_gate not3(and_out, out);
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

// Полусумматор
module half_adder(a, b, sum, c_out);
  input a, b;
  output sum, c_out;

  xor_gate xor1(a, b, sum);
  and_gate and1(a, b, c_out);
endmodule

// Полный сумматор
module full_adder(
  input a, b, cin,
  output sum, cout
);
  
  assign sum = a ^ b ^ cin;
  assign cout = (a & b) | (b & cin) | (a & cin);
endmodule

// 4-битный сумматор/вычитатель
module adder_subtractor_4bit(
    input [3:0] a, 
    input [3:0] b, 
    input sel,  // 0 для сложения, 1 для вычитания
    output [3:0] sum, 
    output carry_out
);
    wire [3:0] b_inverted;
    wire c1, c2, c3; // Промежуточные переносы

    // Инвертируем b для вычитания (если sel = 1)
    assign b_inverted = b ^ {4{sel}};
    
    // Используем полный сумматор для вычисления суммы или разности
    full_adder fa0(a[0], b_inverted[0], sel, sum[0], c1);
    full_adder fa1(a[1], b_inverted[1], c1,  sum[1], c2);
    full_adder fa2(a[2], b_inverted[2], c2,  sum[2], c3);
    full_adder fa3(a[3], b_inverted[3], c3,  sum[3], carry_out);
endmodule

// Модуль ALU
module alu(
    input [3:0] a,       // Операнд A
    input [3:0] b,       // Операнд B
    input [2:0] control, // Управляющий сигнал для выбора операции
    output reg [3:0] res,// Результат операции
    output carry_out     // Перенос для операций сложения/вычитания
);
    wire [3:0] sum;                   // Результат суммирования/вычитания
    wire carry_borrow;                // Перенос или заем
    wire [3:0] and_result, or_result; // Результаты побитовых операций
    wire [3:0] not_b;                 // Инвертированный b
    wire [3:0] and_not_b_result, or_not_b_result; // Результаты a & !b и a | !b
    wire [3:0] subtract_result;       // Результат вычитания для SLT
    wire slt_sign_bit;                // Старший бит для проверки в SLT

    // Инвертируем b
    not_gate not_b0(b[0], not_b[0]);
    not_gate not_b1(b[1], not_b[1]);
    not_gate not_b2(b[2], not_b[2]);
    not_gate not_b3(b[3], not_b[3]);

    // Побитовые логические операции
    and_gate and0(a[0], b[0], and_result[0]);
    and_gate and1(a[1], b[1], and_result[1]);
    and_gate and2(a[2], b[2], and_result[2]);
    and_gate and3(a[3], b[3], and_result[3]);

    or_gate or0(a[0], b[0], or_result[0]);
    or_gate or1(a[1], b[1], or_result[1]);
    or_gate or2(a[2], b[2], or_result[2]);
    or_gate or3(a[3], b[3], or_result[3]);

    // Побитовые операции с инвертированным b
    and_gate and_not_b0(a[0], not_b[0], and_not_b_result[0]);
    and_gate and_not_b1(a[1], not_b[1], and_not_b_result[1]);
    and_gate and_not_b2(a[2], not_b[2], and_not_b_result[2]);
    and_gate and_not_b3(a[3], not_b[3], and_not_b_result[3]);

    or_gate or_not_b0(a[0], not_b[0], or_not_b_result[0]);
    or_gate or_not_b1(a[1], not_b[1], or_not_b_result[1]);
    or_gate or_not_b2(a[2], not_b[2], or_not_b_result[2]);
    or_gate or_not_b3(a[3], not_b[3], or_not_b_result[3]);

    // 4-битный сумматор/вычитатель для операций ADD и SUBTRACT
    adder_subtractor_4bit add_sub (
        .a(a), 
        .b(b), 
        .sel(control == 3'b110 || control == 3'b111),  // Выбираем вычитание для SUBTRACT и SLT
        .sum(sum), 
        .carry_out(carry_borrow)
    );

    // Результат вычитания для SLT
    assign subtract_result = sum;
    assign slt_sign_bit = subtract_result[3]; // Проверяем старший бит (знаковый бит)

    // Логика выбора результата на основе управляющего сигнала
    always @(*) begin
        case (control)
            3'b000: res = and_result;          // a & b
            3'b001: res = or_result;           // a | b
            3'b010: res = sum;                 // a + b
            3'b100: res = and_not_b_result;    // a & !b
            3'b101: res = or_not_b_result;     // a | !b
            3'b110: res = sum;                 // a - b
            3'b111: res = (slt_sign_bit) ? 4'b0001 : 4'b0000; // slt: если знаковый бит результата вычитания = 1, то a < b
            default: res = 4'b0000;
        endcase
    end

    // Передача переноса или заема для сложения и вычитания
    assign carry_out = (control == 3'b010 || control == 3'b110) ? carry_borrow : 1'b0;
endmodule

// D-триггер (d_latch) — ячейка памяти
module d_latch(clk, d, we, q);
  input clk; // Тактовый сигнал
  input d;   // Входные данные для записи в ячейку
  input we;  // Разрешение на запись в ячейку

  output reg q; // Выходное значение ячейки

  // Инициализация. По умолчанию в ячейке хранится 0.
  initial begin
    q <= 0;
  end

  // Изменение значения ячейки на фронте тактового сигнала при спаде
  always @ (negedge clk) begin
    if (we) begin
      q <= d;
    end
  end
endmodule

// Мультиплексор 4 к 1 для выбора регистра при чтении
module mux4to1_4bit(
    input [1:0] sel,
    input [3:0] in0,
    input [3:0] in1,
    input [3:0] in2,
    input [3:0] in3,
    output [3:0] out
);
    wire [3:0] mux_out0, mux_out1;

    // Мультиплексор для младших битов
    or_gate or0_out (in0[0], in1[0], mux_out0[0]);
    or_gate or1_out (in2[0], in3[0], mux_out1[0]);
    or_gate or_final0 (mux_out0[0], mux_out1[0], out[0]);

    // Аналогично для остальных битов
    or_gate or0_out1 (in0[1], in1[1], mux_out0[1]);
    or_gate or1_out1 (in2[1], in3[1], mux_out1[1]);
    or_gate or_final1 (mux_out0[1], mux_out1[1], out[1]);

    or_gate or0_out2 (in0[2], in1[2], mux_out0[2]);
    or_gate or1_out2 (in2[2], in3[2], mux_out1[2]);
    or_gate or_final2 (mux_out0[2], mux_out1[2], out[2]);

    or_gate or0_out3 (in0[3], in1[3], mux_out0[3]);
    or_gate or1_out3 (in2[3], in3[3], mux_out1[3]);
    or_gate or_final3 (mux_out0[3], mux_out1[3], out[3]);

endmodule

// Модуль регистрового файла
module register_file(
    input clk,                   // Тактовый сигнал
    input [1:0] rd_addr,         // Адрес для чтения
    input [1:0] we_addr,         // Адрес для записи
    input [3:0] we_data,         // Данные для записи
    output reg [3:0] rd_data     // Данные, считанные из регистра
);
    // Сигналы разрешения на запись для каждого регистра
    wire we_reg0, we_reg1, we_reg2, we_reg3;

    // Декодирование адреса записи
    wire we_addr0_not, we_addr1_not;
    not_gate not_we0 (we_addr[0], we_addr0_not);
    not_gate not_we1 (we_addr[1], we_addr1_not);

    and_gate we0_gate (we_addr1_not, we_addr0_not, we_reg0);
    and_gate we1_gate (we_addr1_not, we_addr[0], we_reg1);
    and_gate we2_gate (we_addr[1], we_addr0_not, we_reg2);
    and_gate we3_gate (we_addr[1], we_addr[0], we_reg3);

    // Регистры 0-3
    reg [3:0] reg0, reg1, reg2, reg3;

    // Инициализация регистров
    initial begin
        reg0 = 4'b0000;
        reg1 = 4'b0000;
        reg2 = 4'b0000;
        reg3 = 4'b0000;
    end

    // Процесс записи
    always @(negedge clk) begin
        if (we_reg0) reg0 <= we_data;
        if (we_reg1) reg1 <= we_data;
        if (we_reg2) reg2 <= we_data;
        if (we_reg3) reg3 <= we_data;
    end

    // Логика чтения данных
    always @(*) begin
        case (rd_addr)
            2'b00: rd_data = reg0;
            2'b01: rd_data = reg1;
            2'b10: rd_data = reg2;
            2'b11: rd_data = reg3;
            default: rd_data = 4'bzzzz;
        endcase
    end

endmodule

// Модуль калькулятора
module calculator(
    input clk,                   // Тактовый сигнал
    input [1:0] rd_addr,         // Адрес для чтения значения из регистра
    input [3:0] immediate,       // Значение для операции (второй операнд)
    input [1:0] we_addr,         // Адрес для записи результата
    input [2:0] control,         // Управляющие сигналы для выбора операции
    output [3:0] rd_data         // Данные для вывода
);
    wire [3:0] reg_data;         // Данные, считанные из регистра
    wire [3:0] alu_result;       // Результат работы АЛУ
    wire carry_out;              // Перенос из АЛУ

    // Инстанцирование регистрового файла
    register_file reg_file (
        .clk(clk),
        .rd_addr(rd_addr),
        .we_addr(we_addr),
        .we_data(alu_result),
        .rd_data(reg_data)
    );

    // Инстанцирование АЛУ
    alu alu1 (
        .a(reg_data),
        .b(immediate),
        .control(control),
        .res(alu_result),
        .carry_out(carry_out)
    );

    // Вывод данных из регистра
    assign rd_data = reg_data;

endmodule


