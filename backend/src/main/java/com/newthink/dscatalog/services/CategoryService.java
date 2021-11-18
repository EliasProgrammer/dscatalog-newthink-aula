package com.newthink.dscatalog.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newthink.dscatalog.dto.CategoryDTO;
import com.newthink.dscatalog.entities.Category;
import com.newthink.dscatalog.repositories.CategoryRepository;
import com.newthink.dscatalog.services.exceptions.DataBaseException;
import com.newthink.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest){
		Page<Category> pages = repository.findAll(pageRequest);
		return pages.map(CategoryDTO::new);
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

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		
		}catch (EmptyResultDataAccessException erro) {
			throw new ResourceNotFoundException("Id not found " + id);
		
		}catch(DataIntegrityViolationException erro) {
			throw new DataBaseException("Integrity violation");
		}
	}

	

}
