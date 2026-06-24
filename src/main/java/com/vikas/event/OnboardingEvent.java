package com.vikas.event;

import com.vikas.entity.Employee;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnboardingEvent extends ApplicationEvent {
    private final Employee employee;

    public OnboardingEvent(Object source, Employee employee) {
        super(source);
        this.employee = employee;
    }
}
