package vn.hcmute.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response model cho API")
public class Response {
    @Schema(description = "Trạng thái response", example = "true")
    private Boolean status;
    
    @Schema(description = "Thông báo", example = "Thành công")
    private String message;
    
    @Schema(description = "Dữ liệu trả về")
    private Object body;
}
