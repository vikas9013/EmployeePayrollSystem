package com.vikas.service;

import com.vikas.entity.Employee;
import com.vikas.entity.FullTimeEmployee;
import com.vikas.entity.PartTimeEmployee;
import com.vikas.ExceptionHandler.OnboardingException;
import org.springframework.stereotype.Service;

@Service
public class PayrollSetupService {

    /**
     * Simulates registering the employee in the payroll system.
     * In production, replace this body with a real call to your
     * payroll provider (e.g. Razorpay Payroll, Greythr, ADP API).
     *
     * @param employee the newly saved employee
     * @throws OnboardingException if payroll setup fails
     */
    public void setupPayroll(Employee employee) {
        try {
            String payrollSummary = buildPayrollSummary(employee);

            // --- Replace with real Payroll API call ---
            System.out.println("[PayrollSetupService] Payroll configured for "
                    + employee.getName() + " | " + payrollSummary);

        } catch (OnboardingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OnboardingException(
                    "PAYROLL_SETUP",
                    "Failed to configure payroll for: " + employee.getName(),
                    ex
            );
        }
    }

    private String buildPayrollSummary(Employee employee) {

        if (employee instanceof FullTimeEmployee) {

            FullTimeEmployee fte = (FullTimeEmployee) employee;
            return "Type=FULLTIME, MonthlySalary=" + fte.getMonthlySalary();

        } else if (employee instanceof PartTimeEmployee) {

            PartTimeEmployee pte = (PartTimeEmployee) employee;
            return "Type=PARTTIME, HoursWorked=" + pte.getHoursWorked()
                    + ", HourlyRate=" + pte.getHourlyRate();
        }

        return "Type=UNKNOWN";
    }
}
