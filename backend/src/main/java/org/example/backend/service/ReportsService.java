package org.example.backend.service;

import org.example.backend.Repository.SalesRepository;
import org.example.backend.model.dto.BookOrderHistory;
import org.example.backend.model.dto.BookSalesHistory;
import org.example.backend.model.dto.TotalSales;
import org.example.backend.model.dto.customerHistory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ReportsService {
    private final SalesRepository salesRepository;
    ReportsService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }
    public TotalSales getsalesService(){
        return salesRepository.getsales();
    }
    public List<BookSalesHistory> getBookSalesHistoryService(String bookId,String date){
        return salesRepository.getBookSalesHistoryByDate(bookId,date);
    }
    public List<BookSalesHistory> getTopbooksalesService(){
        return salesRepository.getTopbooksales();
    }
    public BookOrderHistory getBookOrderHistoryService(String bookId){
        return salesRepository.getBookOrderHistory(bookId);
    }
    public List<customerHistory> getTopCustomersService(){
        return salesRepository.getTopCustomers();
    }
}
