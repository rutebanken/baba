/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.organisation.rest;

import io.swagger.annotations.Api;
import no.rutebanken.baba.organisation.model.CodeSpace;
import no.rutebanken.baba.organisation.repository.CodeSpaceRepository;
import no.rutebanken.baba.organisation.repository.VersionedEntityRepository;
import no.rutebanken.baba.organisation.rest.dto.CodeSpaceDTO;
import no.rutebanken.baba.organisation.rest.mapper.CodeSpaceMapper;
import no.rutebanken.baba.organisation.rest.mapper.DTOMapper;
import no.rutebanken.baba.organisation.rest.validation.CodeSpaceValidator;
import no.rutebanken.baba.organisation.rest.validation.DTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Produces("application/json")
@Path("code_spaces")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api(tags = {"Code space resource"}, produces = "application/json")
public class CodeSpaceResource extends AnnotatedBaseResource<CodeSpace, CodeSpaceDTO> {


	@Autowired
	private CodeSpaceRepository repository;

	@Autowired
	private CodeSpaceValidator validator;

	@Autowired
	private CodeSpaceMapper mapper;


	@Override
	protected VersionedEntityRepository<CodeSpace> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<CodeSpace, CodeSpaceDTO> getMapper() {
		return mapper;
	}

	@Override
	protected Class<CodeSpace> getEntityClass() {
		return CodeSpace.class;
	}

	@Override
	protected DTOValidator<CodeSpace, CodeSpaceDTO> getValidator() {
		return validator;
	}
}
