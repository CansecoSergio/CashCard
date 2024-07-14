/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.cashcard.controller;

import example.cashcard.model.CashCard;
import example.cashcard.repository.CashCardRepository;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author DGIE - J72 Aprovisionamiento de Tecnología y Datos Económicos
 */
@RestController
@RequestMapping("/cashcards")
class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));

        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        Optional<CashCard> cashCard = findCashCard(requestedId, principal);

        if (cashCard.isPresent()) {
            return ResponseEntity.ok(cashCard.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest,
            UriComponentsBuilder uriComponentsBuilder, Principal principal) {

        CashCard cashCard = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard cashCardSaved = cashCardRepository.save(cashCard);

        URI uriNewCashCard = uriComponentsBuilder.path("/cashcards/{id}")
                .buildAndExpand(cashCardSaved.id()).toUri();

        return ResponseEntity.created(uriNewCashCard).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> updateAnExistingCashCard(@PathVariable Long requestedId,
            @RequestBody CashCard cashCardUpdate, Principal principal) {

        Optional<CashCard> cashCard = findCashCard(requestedId, principal);

        if (cashCard.isPresent()) {
            CashCard cashCardFinal = new CashCard(cashCard.get().id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(cashCardFinal);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }

    private Optional<CashCard> findCashCard(Long cardId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(cardId, principal.getName());
    }

}
