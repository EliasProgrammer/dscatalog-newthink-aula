package com.newthink.dscatalog.services;

import com.newthink.dscatalog.dto.ProductDTO;
import com.newthink.dscatalog.entities.Product;
import com.newthink.dscatalog.repositories.ProductRepository;
import com.newthink.dscatalog.services.exceptions.DataBaseException;
import com.newthink.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		Page<Product> pages = repository.findAll(pageRequest);
		return pages.map(ProductDTO::new);
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product entity =  repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Entity not found"));

		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		//entity.setName(dto.getName());
		entity = repository.save(entity);
		
		return new ProductDTO(entity);
	}
	
	@Transactional
	public ProductDTO update(ProductDTO dto, Long id) {
		try {
			Product entity = repository.getOne(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			
			return new ProductDTO(entity);
		
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
