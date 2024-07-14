package com.magnasha.powerjolt.wsdto;

import com.magnasha.powerjolt.document.TransformationHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
public class TransformationHistoryResponse {
    private LocalDate date;
    private List<TransformationHistory> records;

}
