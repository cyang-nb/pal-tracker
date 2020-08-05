package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    HashMap< String, String> map = new HashMap<>();

    public EnvController(@Value("${PORT:NOT SET}") String port,
                         @Value("${MEMORY_LIMIT:NOT SET}") String memoryLimit,
                         @Value("${cf.instance.index:NOT SET}") String cfInstanceIndex,
                         @Value("${cf.instance.addr:NOT SET}") String cfInstanceAddr) {
        map = new HashMap(){{
            put("PORT",port);
            put("MEMORY_LIMIT",memoryLimit);
            put("CF_INSTANCE_INDEX",cfInstanceIndex);
            put("CF_INSTANCE_ADDR",cfInstanceAddr);
        }};
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return map;
    }
}
