package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.impl;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.CatalogUseCase;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.catalog.MessageCatalog;

@Service
public class CatalogUseCaseImpl implements CatalogUseCase {

	@Override
	public String getMessage(String key) {
		return MessageCatalog.getMessage(key);
	}

	@Override
	public String getMessage(String key, String... params) {
		return MessageCatalog.getMessage(key, params);
	}
}

