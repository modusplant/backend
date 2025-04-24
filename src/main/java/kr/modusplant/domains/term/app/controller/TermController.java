package kr.modusplant.domains.term.app.controller;

import kr.modusplant.domains.term.app.http.request.TermInsertRequest;
import kr.modusplant.domains.term.app.http.request.TermUpdateRequest;
import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.app.service.TermApplicationService;
import kr.modusplant.global.app.servlet.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/crud/terms")
@RequiredArgsConstructor
public class TermController {
    private final TermApplicationService termApplicationService;

    @GetMapping
    public ResponseEntity<DataResponse<List<TermResponse>>> getAllTerms() {
        return ResponseEntity.ok().body(DataResponse.ok(termApplicationService.getAll()));
    }

    @GetMapping("/version/{version}")
    public ResponseEntity<DataResponse<List<TermResponse>>> getTermsByVersion(@PathVariable String version) {
        return ResponseEntity.ok().body(DataResponse.ok(termApplicationService.getByVersion(version)));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> getTermByUuid(@PathVariable UUID uuid) {
        Optional<TermResponse> optionalTermResponse = termApplicationService.getByUuid(uuid);
        if (optionalTermResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalTermResponse.orElseThrow()));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DataResponse<?>> getTermByUuid(@PathVariable String name) {
        Optional<TermResponse> optionalTermResponse = termApplicationService.getByName(name);
        if (optionalTermResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalTermResponse.orElseThrow()));
    }

    @PostMapping
    public ResponseEntity<DataResponse<TermResponse>> insertTerm(@RequestBody TermInsertRequest termInsertRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(termApplicationService.insert(termInsertRequest)));
    }

    @PostMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> updateTerm(@RequestBody TermUpdateRequest termUpdateRequest) {
        return ResponseEntity.ok().body(DataResponse.ok(termApplicationService.update(termUpdateRequest)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<DataResponse<?>> removeTermById(@RequestParam UUID uuid) {
        termApplicationService.removeByUuid(uuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
