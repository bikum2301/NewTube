const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql');
const cors = require('cors');

const app = express();
const port = 3000; // Chọn port bạn muốn

app.use(cors()); // Cho phép CORS (tạm thời cho dev, cấu hình chặt chẽ hơn cho production)
app.use(bodyParser.json()); // Parse JSON request body

// Kết nối database MySQL
const db = mysql.createConnection({
    host: 'localhost', // Thay đổi nếu MySQL server không ở localhost
    user: 'bikum2301', // Thay đổi username MySQL của bạn
    password: 'Dongphuong2301@@', // Thay đổi password MySQL của bạn
    database: 'newtube_db' // Thay đổi tên database của bạn
});

db.connect((err) => {
    if (err) {
        console.error('Lỗi kết nối MySQL: ' + err.stack);
        return;
    }
    console.log('Đã kết nối MySQL thành công với ID ' + db.threadId);
});

// **Các API endpoints sẽ được thêm vào đây**
// ... (đoạn code kết nối database ở trên) ...

// GET /videos - Lấy danh sách tất cả video
app.get('/videos', (req, res) => {
    const sql = 'SELECT * FROM videos';
    db.query(sql, (err, results) => {
        if (err) {
            console.error('Lỗi truy vấn database: ' + err.stack);
            res.status(500).json({ error: 'Lỗi server' });
            return;
        }
        res.json(results); // Trả về danh sách video dưới dạng JSON
    });
});

// GET /videos/:id - Lấy chi tiết video theo ID
app.get('/videos/:id', (req, res) => {
    const videoId = req.params.id;
    const sql = 'SELECT * FROM videos WHERE id = ?';
    db.query(sql, [videoId], (err, results) => {
        if (err) {
            console.error('Lỗi truy vấn database: ' + err.stack);
            res.status(500).json({ error: 'Lỗi server' });
            return;
        }
        if (results.length > 0) {
            res.json(results[0]); // Trả về thông tin video đầu tiên (và duy nhất)
        } else {
            res.status(404).json({ message: 'Không tìm thấy video' });
        }
    });
});

// POST /videos - Thêm video mới
app.post('/videos', (req, res) => {
    const { title, description, video_url, thumbnail_url } = req.body;
    const sql = 'INSERT INTO videos (title, description, video_url, thumbnail_url) VALUES (?, ?, ?, ?)';
    db.query(sql, [title, description, video_url, thumbnail_url], (err, result) => {
        if (err) {
            console.error('Lỗi truy vấn database: ' + err.stack);
            res.status(500).json({ error: 'Lỗi server' });
            return;
        }
        res.status(201).json({ message: 'Video đã được thêm thành công', videoId: result.insertId }); // Trả về ID video mới tạo
    });
});

// PUT /videos/:id - Cập nhật video (ví dụ: chỉ cập nhật title và description)
app.put('/videos/:id', (req, res) => {
    const videoId = req.params.id;
    const { title, description } = req.body; // Chỉ ví dụ cập nhật title và description
    const sql = 'UPDATE videos SET title = ?, description = ? WHERE id = ?';
    db.query(sql, [title, description, videoId], (err, result) => {
        if (err) {
            console.error('Lỗi truy vấn database: ' + err.stack);
            res.status(500).json({ error: 'Lỗi server' });
            return;
        }
        if (result.affectedRows > 0) {
            res.json({ message: 'Video đã được cập nhật thành công' });
        } else {
            res.status(404).json({ message: 'Không tìm thấy video để cập nhật' });
        }
    });
});

// DELETE /videos/:id - Xóa video
app.delete('/videos/:id', (req, res) => {
    const videoId = req.params.id;
    const sql = 'DELETE FROM videos WHERE id = ?';
    db.query(sql, [videoId], (err, result) => {
        if (err) {
            console.error('Lỗi truy vấn database: ' + err.stack);
            res.status(500).json({ error: 'Lỗi server' });
            return;
        }
        if (result.affectedRows > 0) {
            res.json({ message: 'Video đã được xóa thành công' });
        } else {
            res.status(404).json({ message: 'Không tìm thấy video để xóa' });
        }
    });
});

// ... (đoạn code app.listen() ở trên) ...
app.listen(port, () => {
    console.log(`Server API đang chạy trên port ${port}`);
});