package com.pki.example.DTO;

import com.pki.example.validation.ValidFileExtension;
import com.pki.example.validation.ValidFileSize;
import com.pki.example.validation.ValidationConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


public class CsrUploadRequestDTO {

    @NotNull(message = "CSR file is required")
    @ValidFileExtension(extensions = {".csr", ".pem"},
            message = "CSR file must have .csr or .pem extension")
    @ValidFileSize(maxSizeInBytes = ValidationConstants.MAX_FILE_SIZE,
            message = ValidationConstants.FILE_SIZE_EXCEEDED_MSG)
    private MultipartFile csrFile;

    @NotNull(message = "CA selection is required")
    @Positive(message = "Selected CA ID must be positive")
    private Integer selectedCaId;  // PROMENIO SA Long NA Integer

    @NotNull(message = "Duration is required")
    @Min(value = ValidationConstants.MIN_DURATION_DAYS,
            message = "Duration must be at least {value} day")
    @Max(value = ValidationConstants.MAX_DURATION_DAYS,
            message = "Duration cannot exceed {value} days")
    private Integer requestedDurationDays;

    public CsrUploadRequestDTO() {}

    public CsrUploadRequestDTO(MultipartFile csrFile, Integer selectedCaId, Integer requestedDurationDays) {
        this.csrFile = csrFile;
        this.selectedCaId = selectedCaId;
        this.requestedDurationDays = requestedDurationDays;
    }

    public MultipartFile getCsrFile() {
        return csrFile;
    }

    public void setCsrFile(MultipartFile csrFile) {
        this.csrFile = csrFile;
    }


    public Integer getSelectedCaId() {
        return selectedCaId;
    }

    public void setSelectedCaId(Integer selectedCaId) {
        this.selectedCaId = selectedCaId;
    }

    public void setRequestedDurationDays(Integer requestedDurationDays) {
        this.requestedDurationDays = requestedDurationDays;
    }

    public Integer getRequestedDurationDays() { return requestedDurationDays; }
}