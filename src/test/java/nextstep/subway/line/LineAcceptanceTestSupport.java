package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class LineAcceptanceTestSupport {
	LineAcceptanceTestSupport() {
	}

	static ExtractableResponse<Response> 지하철노선_얻기() {
		return RestAssured
				.given().log().all()
				.when().get("/lines")
				.then().log().all().extract();
	}

	static void 지하철노선목록_조회_검사(ExtractableResponse<Response> getResponse,
	                          ExtractableResponse<Response>... createResponse) {
		assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedStationIds = Arrays.stream(createResponse)
				.map(response -> response.header("Location").split("/")[2])
				.map(Long::parseLong)
				.collect(Collectors.toList());
		List<Long> actualStationIds = getResponse.jsonPath().getList(".", LineResponse.class).stream()
				.map(LineResponse::getId)
				.collect(Collectors.toList());
		assertThat(expectedStationIds).containsAll(actualStationIds);
	}

	static ExtractableResponse<Response> 지하철노선_조회(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured
				.given().log().all()
				.when().get(uri)
				.then().log().all().extract();
	}

	static void 지하철노선_조회_검사(ExtractableResponse<Response> createResponse, ExtractableResponse<Response> getResponse) {
		assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		Long expectedId = createResponse.body().as(LineResponse.class).getId();
		Long actualId = getResponse.body().as(LineResponse.class).getId();
		assertThat(actualId).isEqualTo(expectedId);
	}

	static ExtractableResponse<Response> 지하철노선_조회(String uri) {
		return RestAssured
				.given().log().all()
				.when().get(uri)
				.then().log().all().extract();
	}

	static ExtractableResponse<Response> 지하철노선_수정_요청(ExtractableResponse<Response> createResponse,
	                                                 String name, String color) {
		String uri = createResponse.header("Location");

		LineRequest lineRequest = new LineRequest(name, color);
		return RestAssured.given().log().all()
				.body(lineRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().put(uri)
				.then().log().all().extract();
	}

	static ExtractableResponse<Response> 지하철노선_삭제_요청(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured.given().log().all()
				.when().delete(uri)
				.then().log().all().extract();
	}

	static ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color) {
		LineRequest lineRequest = new LineRequest(name, color);

		return RestAssured
				.given().log().all()
				.body(lineRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().post("/lines")
				.then().log().all().extract();
	}

	static void assertStatusCode(ExtractableResponse<Response> response, HttpStatus httpStatus) {
		assertThat(response.statusCode()).isEqualTo(httpStatus.value());
	}
}
