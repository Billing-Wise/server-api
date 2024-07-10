package site.billingwise.api.serverapi.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.File;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class S3Service {

	@Value("${aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	public String upload(MultipartFile multipartFile, String directory) {
		String pathDelimiter = "/";

		String fileName = multipartFile.getOriginalFilename();
		String uuid = UUID.randomUUID().toString();
		String saveFileName = directory + pathDelimiter + uuid + "_" + fileName;

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(multipartFile.getSize());
		try {
			amazonS3.putObject(bucket, saveFileName, multipartFile.getInputStream(), objectMetadata);
		} catch (Exception e) {
			throw new GlobalException(FailureInfo.INVALID_IMAGE);
		}

		return amazonS3.getUrl(bucket, saveFileName).toString();
	}

	public void delete(String filePath, String directory) throws AmazonS3Exception {
		amazonS3.deleteObject(bucket, directory + "/" + new File(filePath).getName());
	}
}
