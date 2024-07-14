/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.cashcard.model;

import org.springframework.data.annotation.Id;

/**
 *
 * @author DGIE - J72 Aprovisionamiento de Tecnología y Datos Económicos
 */
public record CashCard(@Id Long id, Double amount, String owner) {
    
}
