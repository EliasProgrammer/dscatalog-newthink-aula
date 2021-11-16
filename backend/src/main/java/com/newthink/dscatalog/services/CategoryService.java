package com.newthink.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newthink.dscatalog.dto.CategoryDTO;
import com.newthink.dscatalog.entities.Category;
import com.newthink.dscatalog.repositories.CategoryRepository;
import com.newthink.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		return repository.findAll().stream().map(CategoryDTO::new).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Category category =  repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Entity not found"));

		return new CategoryDTO(category);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO update(CategoryDTO dto, Long id) {
		try {
			Category entity = repository.getOne(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			
			return new CategoryDTO(entity);
		
		}catch(EntityNotFoundException erro) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

}
