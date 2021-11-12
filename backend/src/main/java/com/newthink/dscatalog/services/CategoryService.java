package com.newthink.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newthink.dscatalog.dto.CategoryDTO;
import com.newthink.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	public List<CategoryDTO> findAll(){
		return repository.findAll().stream().map(CategoryDTO::new).collect(Collectors.toList());
	}

}
