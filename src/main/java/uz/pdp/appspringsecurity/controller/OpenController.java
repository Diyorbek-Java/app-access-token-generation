package uz.pdp.appspringsecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.appspringsecurity.payload.ApiResult;
import uz.pdp.appspringsecurity.projection.role.RoleCustomProjection;
import uz.pdp.appspringsecurity.repository.RoleRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
public class OpenController {
    private final RoleRepository repository;
    @GetMapping
    public ApiResult<?> getBla() {
        List<Object[]> roleWithCount=repository.getRolesWithCount();
        Map<Object,Integer> map=new LinkedHashMap<>();
        roleWithCount.forEach(objects ->
                map.put(objects[0],Integer.valueOf(objects[1].toString())));
        return ApiResult.successResponse(map);
        }
    @GetMapping("/second")
    public ApiResult<?> getbla2(){
        List<RoleCustomProjection> roleWithCount=repository.getRolesWithCount2();
        return ApiResult.successResponse(roleWithCount);
    }

}
