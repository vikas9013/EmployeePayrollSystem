package com.vikas.service;

import com.vikas.entity.Employee;
import com.vikas.entity.FullTimeEmployee;
import com.vikas.entity.PartTimeEmployee;
import com.vikas.exception.OnboardingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// CHANGED:
//  1. Added @Slf4j — replaces System.out.println with structured logging
//  2. Updated OnboardingException import path to com.vikas.exception (lowercase package)

@Slf4j
@Service
public class PayrollSetupService {

    /**
     * Simulates registering the employee in the payroll system.
     * In production, replace the body with a real call to your
     * payroll provider (e.g. Razorpay Payroll, Greythr, ADP API).
     */
    public void setupPayroll(Employee employee) {
        try {
            String payrollSummary = buildPayrollSummary(employee);
            log.info("[PayrollSetupService] Payroll configured for {} | {}",
                    employee.getName(), payrollSummary);

        } catch (OnboardingException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("[PayrollSetupService] Failed to configure payroll for: {}",
                    employee.getName(), ex);
            throw new OnboardingException(
                    "PAYROLL_SETUP",
                    "Failed to configure payroll for: " + employee.getName(),
                    ex
            );
        }
    }

    private String buildPayrollSummary(Employee employee) {
        if (employee instanceof FullTimeEmployee fte) {
            return "Type=FULLTIME, MonthlySalary=" + fte.getMonthlySalary();
        } else if (employee instanceof PartTimeEmployee pte) {
            return "Type=PARTTIME, HoursWorked=" + pte.getHoursWorked()
                    + ", HourlyRate=" + pte.getHourlyRate();
        }
        return "Type=UNKNOWN";
    }
}