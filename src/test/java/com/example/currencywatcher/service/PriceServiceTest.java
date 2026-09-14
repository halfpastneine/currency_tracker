package com.example.currencywatcher.service;

import com.example.currencywatcher.client.*;
import com.example.currencywatcher.domain.CurrentPriceEntity;
import com.example.currencywatcher.domain.Type;
import com.example.currencywatcher.repository.CurrentPriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private CurrentPriceRepository repository;

    private PriceClient makeCrypto() {
        PriceClient cryptoClient = mock(PriceClient.class);
        when(cryptoClient.is()).thenReturn(Type.CRYPTO);
        when(cryptoClient.getApiName()).thenReturn("COIN GECKO");
        return cryptoClient;
    }

    private PriceClient makeFiat() {
        PriceClient fiatClient = mock(PriceClient.class);
        when(fiatClient.is()).thenReturn(Type.FIAT);
        return fiatClient;
    }

    @Test
    void fetchAndSaveWorks() {
        PriceClient cryptoClient = makeCrypto();
        PriceClient fiatClient = makeFiat();

        when(cryptoClient.fetch("bitcoin", "usd")).thenReturn(new ApiResponse("COIN GECKO", new BigDecimal("65000")));
        when(repository.save(any())).thenAnswer(x -> x.getArgument(0));

        PriceService service = new PriceService(List.of(cryptoClient, fiatClient), repository);
        CurrentPriceEntity saved = service.save(service.fetch(Type.CRYPTO, "bitcoin", "usd"));

        assertThat(saved.getCurrentPrice()).isEqualByComparingTo("65000");
        assertThat(saved.getApiName()).isEqualTo("COIN GECKO");
        verify(fiatClient, never()).fetch(any(), any());
    }

    @Test
    void noClientThrows() {
        PriceService service = new PriceService(List.of(), repository);

        assertThatThrownBy(() -> service.save(service.fetch(Type.FIAT, "eur", "usd")))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void clientExceptionWorks() {
        PriceClient client = makeFiat();
        when(client.fetch(any(), any())).thenThrow(
                new ClientException("boom", "Frankfurter", 500, ClientErrorType.SERVER_ERROR, "eur/usd"));

        PriceService service = new PriceService(List.of(client), repository);

        assertThatThrownBy(() -> service.save(service.fetch(Type.FIAT, "eur", "usd")))
                .isInstanceOf(ClientException.class);
        verifyNoInteractions(repository);
    }
}