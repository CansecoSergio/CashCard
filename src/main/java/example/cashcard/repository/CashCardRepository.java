/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.cashcard.repository;

import example.cashcard.model.CashCard;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author DGIE - J72 Aprovisionamiento de Tecnología y Datos Económicos
 */
public interface CashCardRepository extends CrudRepository<CashCard, Long> {
    
}
