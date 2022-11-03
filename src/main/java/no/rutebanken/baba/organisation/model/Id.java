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

package no.rutebanken.baba.organisation.model;

import com.google.common.base.Joiner;
import org.springframework.util.StringUtils;

public class Id {

	private final String codeSpace;

	private final String type;

	private final String privateCode;

	public static final String SEPARATOR_CHAR = ":";

	public Id(String codeSpace, String type, String privateCode) {
		this.codeSpace = codeSpace;
		this.type = type;
		this.privateCode = privateCode;
	}

	public static Id fromString(String publicId) {
		if (!isValid(publicId)) {
			throw new IllegalArgumentException("Malformed ID: " + publicId);
		}
		String[] parts = publicId.split(":");

		if (parts.length > 2) {
			return new Id(parts[0], parts[1], parts[2]);
		} else if (parts.length == 2) {
			return new Id(null, parts[0], parts[1]);
		}

		return new Id(null, null, publicId);
	}

	private static boolean isValid(String publicId) {
		return StringUtils.hasText(publicId);
	}

	public String getCodeSpace() {
		return codeSpace;
	}

	public String getPrivateCode() {
		return privateCode;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return Joiner.on(SEPARATOR_CHAR).join(codeSpace, privateCode);
	}

}
