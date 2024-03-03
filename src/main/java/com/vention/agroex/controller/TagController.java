package com.vention.agroex.controller;

import com.vention.agroex.dto.Tag;
import com.vention.agroex.service.TagService;
import com.vention.agroex.util.mapper.TagMapper;
import com.vention.agroex.util.validator.TagValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;
    private final TagValidator tagValidator;

    @GetMapping
    public ResponseEntity<List<Tag>> getAll() {
        var tags = tagService.getAll();
        return ResponseEntity.ok(tagMapper.toDtos(tags));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getById(@PathVariable("id") Long id) {
        var tag = tagService.getById(id);
        return ResponseEntity.ok(tagMapper.toDto(tag));
    }

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @PostMapping
    public ResponseEntity<Tag> save(@RequestBody Tag tag,
                                    BindingResult bindingResult) {
        tagValidator.validate(tag, bindingResult);
        var saved = tagService.save(tagMapper.toEntity(tag));
        return ResponseEntity.ok(tagMapper.toDto(saved));
    }

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @PutMapping("/{id}")
    public ResponseEntity<Tag> update(@PathVariable("id") Long id,
                                      @RequestBody @Valid Tag tag,
                                      BindingResult bindingResult) {
        tagValidator.validate(tag, bindingResult);
        var saved = tagService.update(id, tagMapper.toEntity(tag));
        return ResponseEntity.ok(tagMapper.toDto(saved));
    }

}
