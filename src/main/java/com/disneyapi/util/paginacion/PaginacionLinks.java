package com.disneyapi.util.paginacion;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PaginacionLinks {

	public String crearLinkHeader(Page<?> page, UriComponentsBuilder uriBuilder) {
		final StringBuilder linkHeader = new StringBuilder();
		linkHeader.append("");

		if (page.hasNext()) {
			String uri = construirUri(page.getNumber() + 1, page.getSize(), uriBuilder);
			linkHeader.append(construirLinkHeader(uri, "next"));
		}

		if (page.hasPrevious()) {
			String uri = construirUri(page.getNumber() - 1, page.getSize(), uriBuilder);
			agregarComaSiEsNecesario(linkHeader);
			linkHeader.append(construirLinkHeader(uri, "prev"));
		}

		if (!page.isFirst()) {
			String uri = construirUri(0, page.getSize(), uriBuilder);
			agregarComaSiEsNecesario(linkHeader);
			linkHeader.append(construirLinkHeader(uri, "first"));
		}

		if (!page.isLast()) {
			String uri = construirUri(page.getTotalPages() - 1, page.getSize(), uriBuilder);
			agregarComaSiEsNecesario(linkHeader);
			linkHeader.append(construirLinkHeader(uri, "last"));
		}

		return linkHeader.toString();
	}

	private String construirUri(int newPageNumber, int size, UriComponentsBuilder uriBuilder) {
		return uriBuilder.replaceQueryParam("page", newPageNumber).replaceQueryParam("size", size).build().encode()
				.toUriString();
	}

	private String construirLinkHeader(final String uri, final String rel) {
		return "<" + uri + ">; rel=\"" + rel + "\"";
	}

	private void agregarComaSiEsNecesario(final StringBuilder linkHeader) {
		if (linkHeader.length() > 0) {
			linkHeader.append(", ");
		}
	}
}
