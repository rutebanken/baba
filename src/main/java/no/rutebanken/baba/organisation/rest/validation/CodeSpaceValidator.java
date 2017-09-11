package no.rutebanken.baba.organisation.rest.validation;

import no.rutebanken.baba.organisation.model.CodeSpace;
import no.rutebanken.baba.organisation.rest.dto.CodeSpaceDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
@Service
public class CodeSpaceValidator implements DTOValidator<CodeSpace, CodeSpaceDTO> {
	@Override
	public void validateCreate(CodeSpaceDTO dto) {
		Assert.hasLength(dto.privateCode, "privateCode required");
		Assert.hasLength(dto.xmlns, "xmlns required");
		assertCommon(dto);
	}

	@Override
	public void validateUpdate(CodeSpaceDTO dto, CodeSpace entity) {
		assertCommon(dto);
	}

	private void assertCommon(CodeSpaceDTO dto) {
		Assert.hasLength(dto.xmlnsUrl, "xmlnsUrl required");
	}

	@Override
	public void validateDelete(CodeSpace entity) {
		// TODO check whether in user
	}
}
