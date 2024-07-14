/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.cashcard.repository;

import example.cashcard.model.CashCard;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author DGIE - J72 Aprovisionamiento de Tecnología y Datos Económicos
 */
public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    
    Optional<CashCard> findByIdAndOwner(Long id, String owner);
    
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);
 
    boolean existsByIdAndOwner(Long id, String owner);
}
