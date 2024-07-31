package site.billingwise.api.serverapi.domain.stats.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.stats.InvoiceStats;
import site.billingwise.api.serverapi.domain.stats.service.InvoiceStatsService;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stats")
public class InvoiceStatsController {

    private final InvoiceStatsService invoiceStatsService;

    @GetMapping
    public DataResponse<List<InvoiceStats>> getInvoiceStats(
            @RequestParam(required = true, name="typeId") Long typeId,
            @RequestParam(required = true, name="clientId") Long clientId,
            @RequestParam(required = false, name="year") Integer year,
            @RequestParam(required = false, name="month") Integer month) {

        List<InvoiceStats> invoiceStatsList =  invoiceStatsService.getInvoiceStats(year, month, typeId, clientId);

        return new DataResponse<>(SuccessInfo.CONTRACT_LOADED, invoiceStatsList);
    }
}
