package com.newthink.dscatalog.services;

import com.newthink.dscatalog.dto.ProductDTO;
import com.newthink.dscatalog.entities.Category;
import com.newthink.dscatalog.entities.Product;
import com.newthink.dscatalog.repositories.CategoryRepository;
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
import java.util.Set;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		Page<Product> pages = productRepository.findAll(pageRequest);
		return pages.map(ProductDTO::new);
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product entity =  productRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Entity not found"));

		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);

		entity = productRepository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(ProductDTO dto, Long id) {
		try {
			Product entity = productRepository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = productRepository.save(entity);

			return new ProductDTO(entity);

		}catch(EntityNotFoundException erro) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			productRepository.deleteById(id);

		}catch (EmptyResultDataAccessException erro) {
			throw new ResourceNotFoundException("Id not found " + id);

		}catch(DataIntegrityViolationException erro) {
			throw new DataBaseException("Integrity violation");
		}
	}

	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());

		Set<Category> categories = entity.getCategories();
		categories.clear();

		dto.getCategories().forEach(cat -> {
			Category category = categoryRepository.getOne(cat.getId());
			categories.add(category);
		});
	}

}
