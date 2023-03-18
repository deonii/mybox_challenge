package deonii.mybox.service;

import deonii.mybox.data.dto.FileRequestDTO;
import deonii.mybox.data.dto.FileResponseDTO;

import java.io.IOException;

public interface FileService {
    FileResponseDTO uploadFile(FileRequestDTO fileRequestDTO) throws IOException;
}
