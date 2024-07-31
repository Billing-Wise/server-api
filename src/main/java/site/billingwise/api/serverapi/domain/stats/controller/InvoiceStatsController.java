package site.billingwise.api.serverapi.domain.stats.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.stats.dto.InvoiceStatsDto;
import site.billingwise.api.serverapi.domain.stats.service.InvoiceStatsService;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stats")
public class InvoiceStatsController {

    private final InvoiceStatsService invoiceStatsService;

    @GetMapping("/{typeId}")
    public DataResponse<List<InvoiceStatsDto>> getInvoiceStats(
            @PathVariable(name = "typeId") Long typeId,
            @RequestParam(required = false, name = "year") Integer year,
            @RequestParam(required = false, name = "month") Integer month) {

        List<InvoiceStatsDto> invoiceStatsList = invoiceStatsService.getInvoiceStats(typeId, year, month);

        return new DataResponse<>(SuccessInfo.CONTRACT_LOADED, invoiceStatsList);
    }
}
