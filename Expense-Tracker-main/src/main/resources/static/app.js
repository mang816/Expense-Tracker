document.addEventListener("DOMContentLoaded", function () {
    // --- Dark Mode Logic ---
    const themeToggleBtn = document.getElementById('themeToggle');
    if (themeToggleBtn) {
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
            themeToggleBtn.textContent = '☀️ Light Mode';
        }
        themeToggleBtn.addEventListener('click', function () {
            const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
            if (isDark) {
                document.documentElement.removeAttribute('data-theme');
                localStorage.setItem('theme', 'light');
                themeToggleBtn.textContent = '🌙 Dark Mode';
            } else {
                document.documentElement.setAttribute('data-theme', 'dark');
                localStorage.setItem('theme', 'dark');
                themeToggleBtn.textContent = '☀️ Light Mode';
            }
        });
    }

    // --- Repeat Expense Checkbox Logic ---
    const recurringCheckbox = document.getElementById('recurringCheckbox');
    const frequencyDropdown = document.getElementById('recurrenceFrequency');
    if (recurringCheckbox && frequencyDropdown) {
        recurringCheckbox.addEventListener('change', function () {
            frequencyDropdown.disabled = !this.checked;
        });
    }

    // --- Account Form Dynamic Logic ---
    const accountTypeSelect = document.getElementById('accountTypeSelect');
    if (accountTypeSelect) {
        updateAccountForm(); // Run on load

        const addAccountForm = document.getElementById('addAccountForm');
        if (addAccountForm) {
            addAccountForm.addEventListener('submit', function(e) {
                const type = accountTypeSelect.value;
                const mainName = document.getElementById('mainAccountName').value;
                let finalName = mainName;

                if (type === 'Nigerian Bank') {
                    const accNum = document.getElementById('bankNumber').value;
                    if (accNum) finalName += ' - ' + accNum;
                } else if (type === 'Credit Card') {
                    const cardNum = document.getElementById('cardNumber').value;
                    if (cardNum) finalName += ' - ****' + cardNum.slice(-4);
                } else if (type === 'MoMo') {
                    const momoNum = document.getElementById('momoNumber').value;
                    if (momoNum) finalName += ' - ' + momoNum;
                } else if (type === 'PayPal') {
                    const email = document.getElementById('paypalEmail').value;
                    if (email) finalName += ' - ' + email;
                }
                document.getElementById('finalAccountName').value = finalName;
            });
        }
    }

    // --- Income Edit Buttons ---
    document.querySelectorAll('.edit-income-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            document.getElementById('editIncomeForm').action = '/income/update/' + this.getAttribute('data-id');
            document.getElementById('editSource').value = this.getAttribute('data-source');
            document.getElementById('editAmount').value = this.getAttribute('data-amount');
            document.getElementById('editDate').value = this.getAttribute('data-date');
            document.getElementById('editIncomeModal').style.display = 'flex';
        });
    });
});

// --- Global Functions ---
function updateAccountForm() {
    const type = document.getElementById('accountTypeSelect').value;
    document.querySelectorAll('.dynamic-field').forEach(el => el.style.display = 'none');
    if (type === 'Nigerian Bank') document.getElementById('field-bank').style.display = 'block';
    else if (type === 'Credit Card') document.getElementById('field-card').style.display = 'block';
    else if (type === 'MoMo') document.getElementById('field-momo').style.display = 'block';
    else if (type === 'PayPal') document.getElementById('field-paypal').style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

function openEditAccountModal(btn) {
    document.getElementById('editAccountForm').action = '/accounts/' + btn.getAttribute('data-id') + '/edit';
    document.getElementById('editAccName').value = btn.getAttribute('data-name');
    document.getElementById('editAccType').value = btn.getAttribute('data-type');
    document.getElementById('editAccBalance').value = btn.getAttribute('data-balance');
    document.getElementById('editAccountModal').style.display = 'flex';
}

function openEditBudgetModal(btn) {
    document.getElementById('editBudgetForm').action = '/budget/' + btn.getAttribute('data-id') + '/edit';
    document.getElementById('editBudgetCategory').value = btn.getAttribute('data-category');
    document.getElementById('editBudgetLimit').value = btn.getAttribute('data-limit');
    document.getElementById('editBudgetModal').style.display = 'flex';
}

function openEditGoalModal(btn) {
    document.getElementById('editGoalForm').action = '/goals/' + btn.getAttribute('data-id') + '/edit';
    document.getElementById('editGoalName').value = btn.getAttribute('data-name');
    document.getElementById('editGoalTarget').value = btn.getAttribute('data-target');
    document.getElementById('editGoalModal').style.display = 'flex';
}