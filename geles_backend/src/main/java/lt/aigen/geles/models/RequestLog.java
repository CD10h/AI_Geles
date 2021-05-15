package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter @Setter
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotNull
    private Date executionDate;

    @Column(name="request_url")
    private String requestURL;

    private String methodName;

    public RequestLog() {

    }

    public RequestLog(String username, Date executionDate, String requestURL, String methodName) {
        this.username = username;
        this.executionDate = executionDate;
        this.requestURL = requestURL;
        this.methodName = methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLog that = (RequestLog) o;
        return Objects.equals(username, that.username) && Objects.equals(executionDate, that.executionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, executionDate);
    }
}
