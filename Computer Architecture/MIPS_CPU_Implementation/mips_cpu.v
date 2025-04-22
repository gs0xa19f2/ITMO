`include "util.v"

module mips_cpu(
    clk,
    pc,
    pc_new,
    instruction_memory_a,
    instruction_memory_rd,
    data_memory_a,
    data_memory_rd,
    data_memory_we,
    data_memory_wd,
    register_a1,
    register_a2,
    register_a3,
    register_we3,
    register_wd3,
    register_rd1,
    register_rd2
);
    // Сигнал синхронизации
    input clk;
    // Текущее значение регистра PC
    input [31:0] pc;
    // Новое значение регистра PC (адрес следующей команды)
    output [31:0] pc_new;
    // WE для памяти данных
    output data_memory_we;
    // Адреса памяти и данные для записи в память данных
    output [31:0] instruction_memory_a;
    input [31:0] instruction_memory_rd;
    output [31:0] data_memory_a;
    input [31:0] data_memory_rd;
    output [31:0] data_memory_wd;
    // WE3 для регистрового файла
    output register_we3;
    // Номера регистров
    output [4:0] register_a1;
    output [4:0] register_a2;
    output [4:0] register_a3;
    // Данные для записи в регистровый файл
    output [31:0] register_wd3;
    // Данные, полученные в результате чтения из регистрового файла
    input [31:0] register_rd1;
    input [31:0] register_rd2;

    // -----------------------------------------
    // Instruction Fetch
    assign instruction_memory_a = pc;
    wire [31:0] instruction;
    assign instruction = instruction_memory_rd;

    // -----------------------------------------
    // Instruction Decode
    wire [5:0] opcode;
    wire [4:0] rs, rt, rd, shamt;
    wire [5:0] funct;
    wire [15:0] imm;
    wire [25:0] addr;

    assign opcode = instruction[31:26];
    assign rs     = instruction[25:21];
    assign rt     = instruction[20:16];
    assign rd     = instruction[15:11];
    assign shamt  = instruction[10:6];
    assign funct  = instruction[5:0];
    assign imm    = instruction[15:0];
    assign addr   = instruction[25:0];

    // -----------------------------------------
    // Control Unit
    reg RegDst;
    reg ALUSrc;
    reg MemtoReg;
    reg RegWrite;
    reg MemWrite;
    reg Branch;
    reg Jump;
    reg JR; // Новый сигнал для JR
    reg [2:0] ALUControl;

    // ALU Control Codes
    parameter ALU_AND = 3'b000;
    parameter ALU_OR  = 3'b001;
    parameter ALU_ADD = 3'b010;
    parameter ALU_SLL = 3'b011; 
    parameter ALU_SUB = 3'b110;
    parameter ALU_SLT = 3'b111;

    // Opcode Definitions
    parameter OPCODE_RTYPE = 6'b000000;
    parameter OPCODE_LW    = 6'b100011;
    parameter OPCODE_SW    = 6'b101011;
    parameter OPCODE_BEQ   = 6'b000100;
    parameter OPCODE_BNE   = 6'b000101;
    parameter OPCODE_ADDI  = 6'b001000;
    parameter OPCODE_ANDI  = 6'b001100;
    parameter OPCODE_J     = 6'b000010;
    parameter OPCODE_JAL   = 6'b000011;

    // Function Codes
    parameter FUNCT_ADD = 6'b100000;
    parameter FUNCT_SUB = 6'b100010;
    parameter FUNCT_AND = 6'b100100;
    parameter FUNCT_OR  = 6'b100101;
    parameter FUNCT_SLT = 6'b101010;
    parameter FUNCT_JR  = 6'b001000;

    always @(*) begin
        // Установка значений по умолчанию
        RegDst    = 0;
        ALUSrc    = 0;
        MemtoReg  = 0;
        RegWrite  = 0;
        MemWrite  = 0;
        Branch    = 0;
        Jump      = 0;
        JR        = 0; 
        ALUControl = ALU_ADD;

        if (opcode == OPCODE_RTYPE) begin
            if (funct == 6'b000000 && shamt != 0) begin
                // sll: сдвиг влево
                RegDst   = 1;
                RegWrite = 1;
                ALUSrc   = 1; // Используем shamt
                MemtoReg = 0;
                MemWrite = 0;
                Branch   = 0;
                Jump     = 0;
                JR       = 0;
                ALUControl = ALU_SLL;
            end else if (funct == 6'b000000 && rs == 0 && rd == 0 && shamt == 0) begin
                // NOP: не выполнять никаких действий
                RegWrite = 0;
                MemWrite = 0;
                Branch   = 0;
                Jump     = 0;
                JR       = 0;
                ALUControl = ALU_ADD; // Не важно для NOP
            end else begin
                // Обычная R-type инструкция
                RegDst   = 1;
                RegWrite = 1;
                ALUSrc   = 0;
                MemtoReg = 0;
                MemWrite = 0;
                Branch   = 0;
                Jump     = 0;
                JR       = 0;
                case (funct)
                    FUNCT_ADD: ALUControl = ALU_ADD;
                    FUNCT_SUB: ALUControl = ALU_SUB;
                    FUNCT_AND: ALUControl = ALU_AND;
                    FUNCT_OR:  ALUControl = ALU_OR;
                    FUNCT_SLT: ALUControl = ALU_SLT;
                    FUNCT_JR: begin
                        Jump = 1;
                        JR = 1;
                        RegWrite = 0; // JR не записывает в регистры
                        ALUControl = ALU_ADD;
                    end
                    default: ALUControl = ALU_ADD; 
                endcase
            end
        end else begin
            case (opcode)
                OPCODE_LW: begin
                    RegDst   = 0;
                    RegWrite = 1;
                    ALUSrc   = 1;
                    MemtoReg = 1;
                    MemWrite = 0;
                    Branch   = 0;
                    Jump     = 0;
                    ALUControl = ALU_ADD;
                end
                OPCODE_SW: begin
                    RegWrite = 0;
                    ALUSrc   = 1;
                    MemWrite = 1;
                    Branch   = 0;
                    Jump     = 0;
                    ALUControl = ALU_ADD;
                end
                OPCODE_BEQ: begin
                    RegWrite = 0;
                    ALUSrc   = 0;
                    MemWrite = 0;
                    Branch   = 1;
                    Jump     = 0;
                    ALUControl = ALU_SUB;
                end
                OPCODE_BNE: begin
                    RegWrite = 0;
                    ALUSrc   = 0;
                    MemWrite = 0;
                    Branch   = 1;
                    Jump     = 0;
                    ALUControl = ALU_SUB;
                end
                OPCODE_ADDI: begin
                    RegDst   = 0;
                    RegWrite = 1;
                    ALUSrc   = 1;
                    MemtoReg = 0;
                    MemWrite = 0;
                    Branch   = 0;
                    Jump     = 0;
                    ALUControl = ALU_ADD;
                end
                OPCODE_ANDI: begin
                    RegDst   = 0;
                    RegWrite = 1;
                    ALUSrc   = 1;
                    MemtoReg = 0;
                    MemWrite = 0;
                    Branch   = 0;
                    Jump     = 0;
                    ALUControl = ALU_AND;
                end
                OPCODE_J: begin
                    RegWrite = 0;
                    MemWrite = 0;
                    Branch   = 0;
                    Jump     = 1;
                    ALUControl = ALU_ADD;
                end
                OPCODE_JAL: begin
                    RegWrite = 1;
                    MemWrite = 0;
                    Branch   = 0;
                    Jump     = 1;
                    ALUControl = ALU_ADD;
                end
                default: begin
                    // Для неопределенных опкодов
                    RegDst    = 0;
                    ALUSrc    = 0;
                    MemtoReg  = 0;
                    RegWrite  = 0;
                    MemWrite  = 0;
                    Branch    = 0;
                    Jump      = 0;
                    JR        = 0;
                    ALUControl = ALU_ADD; 
                end
            endcase
        end
    end

    // -----------------------------------------
    // Register File Connections
    assign register_a1 = rs;
    assign register_a2 = rt;

    // Determine write register
    wire [4:0] write_reg;
    assign write_reg = (opcode == OPCODE_JAL) ? 5'd31 : (RegDst ? rd : rt);
    assign register_a3 = write_reg;

    // ALU Source B
    wire [31:0] signImm;
    sign_extend sign_extender(.in(imm), .out(signImm));

    wire [31:0] zeroImm;
    assign zeroImm = {16'b0, imm};

    wire [31:0] ALU_in2;
    assign ALU_in2 = (ALUSrc) ? ((opcode == OPCODE_ANDI) ? zeroImm : signImm) :
                     (ALUControl == ALU_SLL) ? {27'd0, shamt} : register_rd2;

    // -----------------------------------------
    // ALU Operation
    reg [31:0] ALU_result;
    reg zero_flag;

    always @(*) begin
        case (ALUControl)
            ALU_ADD: ALU_result = register_rd1 + ALU_in2;
            ALU_SUB: ALU_result = register_rd1 - ALU_in2;
            ALU_AND: ALU_result = register_rd1 & ALU_in2;
            ALU_OR:  ALU_result = register_rd1 | ALU_in2;
            ALU_SLT: ALU_result = ($signed(register_rd1) < $signed(ALU_in2)) ? 32'd1 : 32'd0;
            ALU_SLL: ALU_result = register_rd2 << shamt;  
            default: ALU_result = 32'd0;
        endcase
        zero_flag = (ALU_result == 32'd0) ? 1'b1 : 1'b0;
    end

    // -----------------------------------------
    // Data Memory Connections
    assign data_memory_we = MemWrite;
    assign data_memory_a  = ALU_result;
    assign data_memory_wd = register_rd2;

    // -----------------------------------------
    // Write Back to Register File
    wire [31:0] write_data;
    assign write_data = (opcode == OPCODE_JAL) ? pc + 4 : (MemtoReg ? data_memory_rd : ALU_result);
    assign register_wd3 = write_data;
    assign register_we3 = RegWrite;

    // -----------------------------------------
    // PC Update Logic
    wire [31:0] pc_plus4;
    assign pc_plus4 = pc + 4;

    wire [31:0] signImm_shl2;
    assign signImm_shl2 = {signImm[29:0], 2'b00};

    wire [31:0] branch_target;
    assign branch_target = pc_plus4 + signImm_shl2;

    wire [31:0] jump_address;
    assign jump_address = {pc_plus4[31:28], addr, 2'b00};

    wire branch_taken;
    assign branch_taken = ((opcode == OPCODE_BEQ) && Branch && zero_flag) ||
                          ((opcode == OPCODE_BNE) && Branch && ~zero_flag);

    assign pc_new = Jump ? (JR ? register_rd1 : jump_address) :
                    (branch_taken ? branch_target : pc_plus4);

endmodule

