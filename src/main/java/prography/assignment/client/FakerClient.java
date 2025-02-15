package prography.assignment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import prography.assignment.client.dto.response.FakerResponse;

@FeignClient(name = "fakerClient", url = "https://fakerapi.it")
public interface FakerClient {

    @GetMapping(value = "/api/v1/users", params = "_locale=ko_KR")
    FakerResponse getData(
            @RequestParam("_seed") int seed,
            @RequestParam("_quantity") int quantity
    );
}
