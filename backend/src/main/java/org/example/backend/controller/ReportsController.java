package org.example.backend.controller;

import org.example.backend.model.dto.BookOrderHistory;
import org.example.backend.model.dto.BookSalesHistory;
import org.example.backend.model.dto.TotalSales;
import org.example.backend.model.dto.customerHistory;
import org.example.backend.service.ReportsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/user/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportsController {
    private final ReportsService reportsService;
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/totalsales")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TotalSales> getsales() {
        TotalSales history = reportsService.getsalesService();
        return ResponseEntity.ok(history);
    }
    @GetMapping("/t")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookSalesHistory>> getBooksales(
            @RequestParam(required = false) String date) {
        List<BookSalesHistory> history = reportsService.getBookSalesHistoryService("0", date);
        return ResponseEntity.ok(history);
    }
    @GetMapping("orderd/{bookId}")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookOrderHistory> getBookorders(
            @PathVariable String bookId) {

        BookOrderHistory history =
                reportsService.getBookOrderHistoryService(bookId);

        if (history == null) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(history); // 200
    }

    @GetMapping("/TopBooksales")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookSalesHistory>> getTopBooksales() {
        List<BookSalesHistory> history = reportsService.getTopbooksalesService();
        return ResponseEntity.ok(history);
    }
    @GetMapping("/TopBookCustomers")
 //   @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<customerHistory>> getTopCustomers() {
        List<customerHistory> history = reportsService.getTopCustomersService();
        return ResponseEntity.ok(history);
    }


}
