class BankAccount(val amount: Int) {
    var balance: Int = amount
        private set(value) {
            logTransaction(field, value)
            field = value
        }

    init {
        require(amount > 0) {
            "Initial balance: $amount must be greater than zero"
        }
    }

    fun deposit(dep: Int) {
        require(dep > 0) {
            "Deposit: $dep must be greater than zero"
        }
        balance += dep
    }

    fun withdraw(with: Int) {
        require(with > 0) {
            "Withdraw: $with must be greater than zero"
        }
        require(with <= balance) {
            "Withdraw: $with cannot be greater than the balance: $balance"
        }
        balance -= with
    }
}
