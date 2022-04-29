package com.banking.account.cmd.api.controllers;

import com.banking.account.cmd.api.command.DepositFoundCommand;
import com.banking.account.common.dto.BaseResponse;
import com.banking.cqrs.core.exceptions.AggregateNotFoundException;
import com.banking.cqrs.core.infrastructure.CommandDispatcher;
import com.banking.cqrs.core.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/v1/depositFunds")
public class DepositedFundsController {

    private final Logger logger = Logger.getLogger(DepositedFundsController.class.getName());

    @Autowired
    private CommandDispatcher commandDispatcher;


    @PutMapping(path = "/{id}")
    public ResponseEntity<BaseResponse> depositFounds(
            @PathVariable(value = "id") String id,
            @RequestBody DepositFoundCommand command) {
        try {
            command.setId(id);
            commandDispatcher.send(command);
            return new ResponseEntity<>(new BaseResponse("El deposito de dinero fue exitoso"), HttpStatus.OK);
        } catch (IllegalStateException | AggregateNotFoundException e) {
            logger.log(Level.WARNING, MessageFormat.format("El cliente envio un request con errores {0}", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrorMessage = MessageFormat.format("Errores mientras se procesaba el request {0}", id);
            return new ResponseEntity<>(new BaseResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
