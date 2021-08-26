package com.example.springbatchflowcontrol.db;

import com.example.springbatchflowcontrol.db.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Modifying
    @Query("update Account a set a.balance = a.balance - :amount where a.accountNumber=:id")
    void subtractBalance(@Param(value="id") String id, @Param(value="amount")BigDecimal amount);

}
