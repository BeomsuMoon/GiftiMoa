const express = require('express');
const nodemailer = require('nodemailer');
const crypto = require('crypto');
const app = express();
const mysql = require('mysql2/promise'); // mysql2/promise 라이브러리를 가져옵니다.
const bcrypt = require('bcrypt');
app.use(express.json());

const pool = mysql.createPool({
    host: 'gm-db.cyxuwglghqsn.ap-northeast-2.rds.amazonaws.com',
    user: 'root',
    password: 'rootroot',
    database: 'gm',
    charset: 'utf8mb4',
    connectionLimit: 10, // 필요에 따라 조절
});

const server = require("http").createServer(app);
const cors = require("cors");
const { request } = require('http');
app.use(cors());

const hostname = '0.0.0.0'; // 변경하려는 호스트 주소
const port = 3306;

server.listen(port, hostname, () => {
  console.log(`서버가 http://${hostname}:${port}/ 에서 실행 중입니다.`);
});

server.listen(port, () => {
    console.log(port + ' 포트에서 서버가 실행 중입니다.');
});

app.get("/", function (req, res) {
    console.log("main");
    res.send('Welcome to the demo application!');
});

app.get("/test", function (req, res) {
    console.log("hello");
    res.send('test log');
});

app.get('/login_node', async (req, res) => {
  const { email, password } = req.query;

  try {
      const [rows] = await pool.query('SELECT password, salt FROM member_user WHERE email = ?', [email]);

      if (rows.length === 1) {
          const storedPassword = rows[0].password;
          const salt = rows[0].salt;

          // 저장된 해시와 검증할 해시를 비교하기 위해 입력된 비밀번호를 해시화합니다.
          bcrypt.hash(password, salt, (err, hashedPasswordToCheck) => {
              if (err) {
                  console.error('비밀번호 해시 검증 오류:', err);
                  res.status(500).json({ result: 2, message: '내부 서버 오류' });
              } else {
                  // 해시화된 비밀번호를 저장된 비밀번호와 비교합니다.
                  if (hashedPasswordToCheck === storedPassword) {
                      console.log("로그인 성공");
                      res.json({ result: 0, message: '로그인 성공' });
                  } else {
                      console.log("유효하지 않은 비밀번호");
                      res.json({ result: 1, message: '유효하지 않은 비밀번호' });
                  }
              }
          });
      } else {
          console.log("일치하는 사용자 없음");
          res.json({ result: 1, message: '일치하는 사용자 없음' });
      }
  } catch (err) {
      console.error('데이터베이스 쿼리 오류:', err);
      res.status(500).json({ result: 2, message: '내부 서버 오류' });
  }
});

const saltRounds = 10; // 솔트 생성 라운드 수 (조정 가능)

app.post('/signup_node', async (req, res) => {
    const { email, password, phone_number, name, username } = req.body;

    try {
        // Generate a random salt
        bcrypt.genSalt(saltRounds, async (saltErr, salt) => {
            if (saltErr) {
                console.error('솔트 생성 오류:', saltErr);
                res.status(500).json({ result: 2, message: '내부 서버 오류' });
                return;
            }

            // 비밀번호를 해시화 using the generated salt
            bcrypt.hash(password, salt, async (err, hashedPassword) => {
                if (err) {
                    console.error('비밀번호 해시 생성 오류:', err);
                    res.status(500).json({ result: 2, message: '내부 서버 오류' });
                    return;
                }

                try {
                    const [result] = await pool.query('INSERT INTO gm.member_user (email, password, salt, phone_number, name, username) VALUES (?, ?, ?, ?, ?, ?)', [email, hashedPassword,salt, phone_number, name, username ]);

                    if (result.affectedRows > 0) {
                        console.log("User registered successfully");
                        res.json({ result: 0, message: 'User registered successfully' });
                    } else {
                        console.log("Failed to register user");
                        res.json({ result: 1, message: 'Failed to register user' });
                    }
                } catch (err) {
                    console.error('데이터베이스 쿼리 오류:', err);
                    res.status(500).json({ result: 2, message: '내부 서버 오류' });
                }
            });
        });
    } catch (err) {
        console.error('비밀번호 해시 생성 오류:', err);
        res.status(500).json({ result: 2, message: '내부 서버 오류' });
    }
});

app.post('/updatePassword', async (req, res) => {
  try {
    const { email, newPassword } = req.body;

    // 사용자의 이메일로부터 현재 저장된 솔트 가져오기
    const [userRows] = await pool.query('SELECT salt FROM member_user WHERE email = ?', [email]);

    if (userRows.length === 1) {
      const currentSalt = userRows[0].salt;

      // 새로운 솔트 생성
      const newSalt = await bcrypt.genSalt(10);

      // 새 비밀번호를 새 솔트를 사용하여 해싱
      const hashedPassword = await bcrypt.hash(newPassword, newSalt);

      // 데이터베이스를 새로운 해싱된 비밀번호와 솔트로 업데이트
      const updateQuery = 'UPDATE member_user SET password = ?, salt = ? WHERE email = ?';
      const [updateResult] = await pool.query(updateQuery, [hashedPassword, newSalt, email]);

      if (updateResult.affectedRows > 0) {
        res.json({ error: false, message: '비밀번호가 성공적으로 업데이트되었습니다.' });
        console.log('[비밀번호 변경] 비밀번호가 성공적으로 업데이트 되었습니다.')
      } else {
        res.status(404).json({ error: true, message: '사용자를 찾을 수 없습니다.' });
        console.log('[비밀번호 변경] 사용자를 찾을수 없습니다.')
      }
    } else {
      res.status(404).json({ error: true, message: '일치하는 사용자 없음' });
    }
  } catch (error) {
    console.error('에러:', error);
    res.status(500).json({ error: true, message: '내부 서버 오류' });
  }
});

app.post('/checkPhoneNumber', async (req, res) => {
  const { phone_number } = req.body;

  try {
    const [rows] = await pool.query('SELECT * FROM member_user WHERE phone_number = ? ', [phone_number]);

    if (rows.length > 0) {
      console.log("중복된 번호입니다.");
      res.json({ status: '중복' });
    } else {
      console.log("중복되지 않은 번호입니다.");
      res.json({ status: '중복 아님' });
    }
  } catch (err) {
    console.error('데이터베이스 쿼리 오류:', err);
    res.status(500).json({ error: '데이터베이스 오류' });
  }
});

app.post('/checkNickName', async (req, res) => {
  const { username } = req.body;

  try {
    const [rows] = await pool.query('SELECT * FROM member_user WHERE username = ? ', [username]);

    if (rows.length > 0) {
      console.log("중복된 닉네임입니다.");
      res.json({ status: '중복' });
    } else {
      console.log("중복되지 않은 닉네임입니다.");
      res.json({ status: '중복 아님' });
    }
  } catch (err) {
    console.error('데이터베이스 쿼리 오류:', err);
    res.status(500).json({ error: '데이터베이스 오류' });
  }
});

app.post('/idFind', async (req, res) => {
  const phoneNumber = req.body.phoneNumber;

  try {
    const user = await findUserByEmail(phoneNumber);
    if (user) {
      // 여기서 user는 배열입니다.
      // user 배열의 첫 번째 요소에서 email 값을 추출합니다.
      const userEmail = user[0] ? user[0].email : null;

      // userEmail이 null이 아니면 해당 email 값을 반환합니다.
      if (userEmail) {
        res.json({ email: userEmail });
        console.log("[아이디 찾기] : 이메일 찾기 성공" + userEmail);
      } else {
        // userEmail이 null이면 사용자를 찾지 못한 것으로 간주하여 404 에러를 반환합니다.
        res.status(404).json({ error: 'User not found' });
        console.log("[아이디 찾기] : 이메일 찾기 실패");
      }
    } else {
      res.status(404).json({ error: 'User not found' });
      console.log("[아이디 찾기] : 이메일 찾기 실패");
    }
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// MariaDB에서 사용자 조회
async function findUserByEmail(phoneNumber) {
  let conn;
  try {
    conn = await pool.getConnection();
    const [rows] = await conn.query('SELECT email FROM member_user WHERE phone_number = ?', [phoneNumber]);

    if (rows.length > 0) {
      // 여러 사용자가 검색될 수 있으므로 배열로 모두 반환
      return rows;
    } else {
      return null; // 사용자가 없을 경우 null 반환
    }
  } finally {
    if (conn) conn.release();
  }
}

app.post('/forgotPassword', (req, res) => {
  const { username } = req.body;

  // 데이터베이스에서 아이디 확인
  pool.query('SELECT email FROM member_user WHERE username = ?', [username], (error, results) => {
      if (error) {
          console.error('데이터베이스 오류:', error);
          res.status(500).json({ error: '데이터베이스 오류' });
      } else if (results.length > 0) {
          const email = results[0].email;
          const verificationCode = generateRandomCode(); // 인증 코드 생성
  sendEmail(email, verificationCode); // 이메일로 인증 코드 전송
          res.json({ message: '이메일은 사용 가능하며 인증 코드를 이메일로 전송했습니다.' });
      } else {
          res.status(404).json({ message: '일치하는 사용자 없음' });
      }
  });
});

app.post('/checkEmail', async (req, res) => {
  const { email } = req.body;

  // 데이터베이스에서 이메일을 확인하는 로직
  let conn;
  try {
      conn = await pool.getConnection();

      // MariaDB에서 사용자 조회
      const [results] = await conn.query('SELECT * FROM member_user WHERE email = ?', [email]);

      // 이메일이 일치하는 경우
      if (results.length > 0) {
          const verificationCode = generateRandomCode();
          // 이메일 전송
          sendEmail(email, verificationCode);
          res.json({ message: '이메일이 확인되었고, 인증 코드를 이메일로 전송했습니다.' });

      } else {
          res.status(404).json({ error: '일치하는 이메일이 없습니다.' });
      }
  } catch (err) {
    console.error('데이터베이스 오류:', err);
    res.status(500).json({ error: '데이터베이스 오류' });
} finally {
    if (conn) conn.release();
}
});

app.post('/EmailNumbersend_node', (req, res) => {
    const { email } = req.body;
    let emailcode; //인증번호를 저장하는 글로벌 함수를 지정합니다.
  
   // 이메일 유효성 검사를 위한 정규식
   const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;

   if (!emailRegex.test(email)) {
       // 이메일 형식이 유효하지 않음
       return res.status(400).json({ error: '유효하지 않은 이메일 형식입니다.' });
   }

    // 데이터베이스에서 이메일 중복 확인
    pool.query('SELECT * FROM member_user WHERE email = ?', [email])
      .then(([results]) => {
        if (results.length > 0) {
          res.status(400).json({ error: '중복된 이메일입니다.' });
        } else {
          // 중복되지 않은 경우에만 인증 코드 생성 및 전송
          const verificationCode = generateRandomCode();
          sendEmail(email, verificationCode);
          res.json({ message: '이메일은 사용 가능하며 인증 코드를 이메일로 전송했습니다.' });
        }
      })
      .catch(err => {
        console.error('데이터베이스 오류:', err);
        res.status(500).json({ error: '데이터베이스 오류' });
      });
  });
  
  function generateRandomCode() {
    const length = 6;
    const characters = '0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
      const randomIndex = Math.floor(Math.random() * characters.length);
      result += characters[randomIndex];
    }
    emailcode = result;
    return result;
  }
  
  function sendEmail(email, verificationCode) {
    const transporter = nodemailer.createTransport({
      service: 'Gmail', // 이메일 서비스 제공자
      auth: {
        user: 'moonkimgm@gmail.com', // 여기에 발신자 이메일 주소 입력
        pass: 'agwd ifrg uisw rqcz', // 여기에 이메일 계정 비밀번호 입력
      },
    });
  
    const mailOptions = {
      from: 'moonkimgm@gmail.com',
      to: email,
      subject: '인증 코드',
      text: `인증 코드: ${verificationCode}`,
    };
    
    transporter.sendMail(mailOptions, (error, info) => {
      if (error) {
        console.log('이메일 전송 실패: ' + error);
      } else {
        console.log('이메일 전송 성공: ' + info.response);
      }
    });
  }

  app.post('/SignUpEmail_node', (req, res) => {
    const { verCode } = req.body;

    if (verCode === emailcode) {
        // 인증 성공
        res.status(200).json({ message: "Verification successful" });
    } else {
        // 인증 실패
        res.status(400).json({ error: "Verification failed" });
    }
});

app.post('/collect_gift_add', async (req, res) => {
  const { giftName, effectiveDate, barcode, usage, imageUrl, state, userEmail } = req.body;

  try {
      const [result] = await pool.query('INSERT INTO gm.collect_gift ( gift_name, effective_date, barcode, usage_description, image_url, state, email) VALUES ( ?, ?, ?, ?, ?, ?, ?)', [ giftName, effectiveDate, barcode, usage, imageUrl, state, userEmail]);

      if (result.affectedRows > 0) {
          console.log("[기프티콘 등록 완료]");
          res.json({ result: 0, message: 'User registered successfully' });
      } else {
          console.log("기프티콘 등록 실패");
          res.json({ result: 1, message: 'Failed to register user' });
      }
  } catch (err) {
      console.error('데이터베이스 쿼리 오류:', err);
      res.status(500).json({ result: 2, message: '내부 서버 오류' });
  }
});

app.post('/home_gift_add', async (req, res) => {
  const { h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl, h_state, favorite, userEmail, nickname } = req.body;
  console.log(req.body);

  try {
      const [result] = await pool.query('INSERT INTO gm.home_gift ( h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl, h_state, favorite, email, username) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', [ h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl, h_state, favorite, userEmail,nickname]);

      if (result.affectedRows > 0) {
          console.log("[기프티콘 등록 완료]");
          res.json({ result: 0, message: 'User registered successfully' });
      } else {
          console.log("기프티콘 등록 실패");
          res.json({ result: 1, message: 'Failed to register user' });
      }
  } catch (err) {
      console.error('데이터베이스 쿼리 오류:', err);
      res.status(500).json({ result: 2, message: '내부 서버 오류' });
  }
});

app.post('/getNicknameByEmail', async (req, res) => {
  const { email } = req.body;

  try {
      const [rows] = await pool.query('SELECT username FROM member_user WHERE email = ?', [email]);

      if (rows.length === 1) {
          const username = rows[0].username;
          res.json(username);
          console.log(`[이메일로 닉네임 찾기] : ${email}에 대한 닉네임은 ${username}입니다.`);
      } else {
          console.log("[이메일로 닉네임 찾기] 일치하는 사용자 없음");
          res.status(404).json({ error: '일치하는 사용자 없음' });
      }
  } catch (err) {
      console.error('[이메일로 닉네임 찾기] 데이터베이스 쿼리 오류:', err);
      res.status(500).json({ error: '내부 서버 오류' });
  }
});


app.get('/getGiftList', async (req, res) => {
  try {
    const userEmail = req.query.email;
    console.log(`Received request for userEmail: ${userEmail}`);

    var sql = `SELECT id, gift_name, effective_date, barcode, usage_description, image_url, state FROM collect_gift WHERE email = "${userEmail}"`;
    const [queryResult] = await pool.query(sql);

    if (queryResult.length > 0) {
      console.log('Sending data:', queryResult);
      
      res.json(queryResult);
    } else {
      console.log('No data found for userEmail:', userEmail);
      res.status(404).json({ error: 'No data found for the specified email' });
    }
  } catch (error) {
    console.error('Error executing SQL query:', error);
    res.status(500).json({ error: 'Failed to fetch data from the server' });
  }
});

app.get('/homeGifts', async (req, res) => {
  try {
    var sql = `SELECT h_id, h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl, h_state, favorite, username  FROM home_gift`;
    const [queryResult] = await pool.query(sql);

    if (queryResult.length > 0) {
      console.log('Sending data:', queryResult);
      
      res.json(queryResult);
    } else {
      console.log('No data found for db:', queryResult);
      res.status(404).json({ error: 'No data found for the specified email' });
    }
  } catch (error) {
    console.error('Error executing SQL query:', error);
    res.status(500).json({ error: 'Failed to fetch data from the server' });
  }
});

app.get('/homeGifts_my', async (req, res) => {
  try {
    const userEmail = req.query.email;
    console.log(`Received request for userEmail: ${userEmail}`);

    var sql = `SELECT h_id, h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl, h_state, favorite, username  FROM home_gift WHERE email= "${userEmail}"`;
    const [queryResult] = await pool.query(sql);

    if (queryResult.length > 0) {
      console.log('Sending data:', queryResult);
      
      res.json(queryResult);
    } else {
      console.log('No data found for userEmail:', userEmail);
      res.status(404).json({ error: 'No data found for the specified email' });
    }
  } catch (error) {
    console.error('Error executing SQL query:', error);
    res.status(500).json({ error: 'Failed to fetch data from the server' });
  }
});

app.delete('/deleteGift', async (req, res) => {
  const { ID } = req.query;

  if (!ID) {
    return res.status(400).json({ error: 'ID 제공되지 않았습니다.' });
  }

  let conn;
  try {
    conn = await pool.getConnection();

    // MariaDB에서 Gift 찾기
    const selectResult = await conn.query('SELECT * FROM collect_gift WHERE id = ?', [ID]);

    if (selectResult.length === 0) {
      console.log(`Gift 찾기 실패: ID ${ID}에 대한 Gift를 찾을 수 없습니다.`);
      res.status(200).json({ message: '해당 ID에 대한 Gift를 찾을 수 없습니다.' });
    } else {
      // Gift를 찾은 경우에만 삭제 진행
      const result = await conn.query('DELETE FROM collect_gift WHERE id = ?', [ID]);

      if (result.affectedRows === 1) {
        console.log('Gift 삭제 성공');
        res.status(200).json({ message: 'Gift 삭제 성공', deletedGiftID: ID });
      } else {
        console.log(`Gift 삭제 실패: ID ${ID}에 대한 Gift를 삭제할 수 없습니다.`);
        res.status(500).json({ error: 'Gift 삭제 중 오류 발생' });
      }
    }
  } catch (error) {
    console.error('Gift 삭제 실패:', error);
    res.status(500).json({ error: 'Gift 삭제 중 오류 발생' });
  } finally {
    if (conn) conn.release(); // 연결 반환
  }
});

app.delete('/deletehomeGift/:h_id', async (req, res) => {
  const h_id = req.params.h_id;
  console.log("delete 업데이트 h_id : ", h_id)

  if (!h_id) {
    return res.status(400).json({ error: 'h_id 제공되지 않았습니다.' });
  }

  let conn;
  try {
    conn = await pool.getConnection();

    // MariaDB에서 Gift 찾기
    const selectResult = await conn.query('SELECT * FROM home_gift WHERE h_id = ?', [h_id]);

    if (selectResult.length === 0) {
      console.log(`Gift 찾기 실패: ID ${h_id}에 대한 Gift를 찾을 수 없습니다.`);
      res.status(200).json({ message: '해당 ID에 대한 Gift를 찾을 수 없습니다.' });
    } else {
      // Gift를 찾은 경우에만 삭제 진행
      const result = await conn.query('DELETE FROM home_gift WHERE h_id = ?', [h_id]);

      if (result.affectedRows === 1) {
        console.log('Gift 삭제 성공');
        res.status(200).json({ message: 'Gift 삭제 성공', deletedGiftID: h_id });
      } else {
        console.log(`Gift 삭제 실패: ID ${h_id}에 대한 Gift를 삭제할 수 없습니다.`);
        res.status(500).json({ error: 'Gift 삭제 중 오류 발생' });
      }
    }
  } catch (error) {
    console.error('Gift 삭제 실패:', error);
    res.status(500).json({ error: 'Gift 삭제 중 오류 발생' });
  } finally {
    if (conn) conn.release(); // 연결 반환
  }
});

app.put('/update_gift/:ID', async (req, res) => {
  const ID = req.params.ID;
  console.log("기프트 업데이트 ID : ", ID);
  const { gift_name, effective_date, barcode, usage_description, image, state } = req.body;
  console.log("기프트 업데이트 리스트 : ", {
    gift_name,
    effective_date,
    barcode,
    usage_description,
    image,
    state
  });

  try {
    const connection = await pool.getConnection();

    // ID를 사용하여 먼저 해당하는 기프트를 찾습니다.
    const selectResult = await connection.query('SELECT * FROM collect_gift WHERE id = ?', [ID]);

    // 만약 id에 해당하는 기프트가 없으면 에러를 응답합니다.
    if (selectResult.length === 0) {
      console.log('Gift not found.');
      res.status(404).json({ success: false, message: 'Gift not found.' });
      return;
    }

    // 기프트가 있는 경우에만 업데이트를 진행합니다.
    const updateQuery = `
      UPDATE collect_gift 
      SET gift_name=?, effective_date=?, barcode=?, usage_description=?, image_url=?, state=? 
      WHERE id=?
    `;

    await connection.execute(updateQuery, [gift_name, effective_date, barcode, usage_description, image, state, ID]);

    connection.release();

    // 업데이트가 성공했을 때
    console.log('Gift updated successfully.');
    res.status(200).json({ success: true, message: 'Gift updated successfully.' });
  } catch (error) {
    console.error('Error updating gift:', error);
    res.status(500).json({ success: false, message: 'Internal server error.' });
  }
});

app.put('/update_home_gift/:h_id', async (req, res) => {
  const h_id = req.params.h_id;
  console.log("홈 기프트 업데이트 ID: ", h_id);
  const {h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl} = req.body;
  console.log("홈 기프트 업데이트 리스트: ", {
    h_product_name,
    h_effectiveDate,
    h_price,
    h_category,
    h_brand,
    h_product_description,
    h_imageUrl
  });

  try {
    const connection = await pool.getConnection();

    // ID를 사용하여 먼저 해당하는 홈 기프트를 찾습니다.
    const selectResult = await connection.query('SELECT * FROM home_gift WHERE h_id = ?', [h_id]);

    // 만약 id에 해당하는 홈 기프트가 없으면 에러를 응답합니다.
    if (selectResult.length === 0) {
      console.log('Home gift not found.');
      res.status(404).json({ success: false, message: 'Home gift not found.' });
      return;
    }

    // 홈 기프트가 있는 경우에만 업데이트를 진행합니다.
    const updateQuery = `
      UPDATE home_gift 
      SET h_product_name=?, h_effectiveDate=?, h_price=?, h_category=?, h_brand=?, h_product_description=?, h_imageUrl=? 
      WHERE h_id=?
    `;

    await connection.execute(updateQuery, [h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl, h_id]);

    connection.release();

    // 업데이트가 성공했을 때
    console.log('Home gift updated successfully.');
    res.status(200).json({ success: true, message: 'Home gift updated successfully.' });
  } catch (error) {
    console.error('Error updating home gift:', error);
    res.status(500).json({ success: false, message: 'Internal server error.' });
  }
});

//HOME gift 관련 카테고리 별로 데이터 가져오기
  app.get('/Categorybrand', async (req, res) => {
    try {
      const brandName = req.query.brand;
      console.log(`Received request for brandName: ${brandName}`);
  
      var sql = `SELECT h_id, h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl, h_state, favorite, username  FROM home_gift WHERE h_brand = "${brandName}"`;
      const [queryResult] = await pool.query(sql);
  
      if (queryResult.length > 0) {
        console.log('[카테고리 브랜드] : [데이터 가져오기 성공]', queryResult);
        
        res.json(queryResult);
      } else {
        console.log('[카테고리 브랜드  브랜드 이름을 찾을수 없음 : ]:', brandName);
        res.status(404).json({ error: 'No data found for the specified brand' });
      }
    } catch (error) {
      console.error('Error executing SQL query:', error);
      res.status(500).json({ error: 'Failed to fetch data from the server' });
    }
  });


  // 회원탈퇴 처리 
app.post('/delete_member_user', async (req, res) => {
  const email = req.body.email; // 클라이언트로부터 전송된 이메일 정보

  // 트랜잭션 시작
  let conn;
  try {
    conn = await pool.getConnection();
    await conn.beginTransaction();

    // member_user 테이블에서 해당 이메일에 관련된 정보 삭제
    await conn.query('DELETE FROM member_user WHERE email = ?', [email]);

    // collect_gift 테이블에서 해당 이메일에 관련된 정보 삭제
    await conn.query('DELETE FROM collect_gift WHERE email = ?', [email]);

    // home_gift 테이블에서 해당 이메일에 관련된 정보 삭제
    await conn.query('DELETE FROM home_gift WHERE email = ?', [email]);

    // 트랜잭션 커밋
    await conn.commit();

    res.json({ message: '회원탈퇴가 성공적으로 처리되었습니다.' });
  } catch (err) {
    // 트랜잭션 롤백
    if (conn) await conn.rollback();

    console.error(err);
    res.status(500).json({ error: '서버 오류로 인해 회원탈퇴를 처리할 수 없습니다.' });
  } finally {
    if (conn) conn.release(); // 연결 해제
  }
});

// 프로필 업데이트 라우터
app.post('/profile_update', async (req, res) => {
  try {
    const { email, username, Profile_picture } = req.body;

    // MariaDB 연결
    const connection = await pool.getConnection();

    // member_user 테이블 업데이트
    const updateQuery = `UPDATE member_user SET username = ?, Profile_picture = ? WHERE email = ?`;

    await connection.query(updateQuery, [username, Profile_picture, email]);

    // 연결 해제
    connection.release();

    res.status(200).json({ message: '프로필이 성공적으로 업데이트되었습니다.' });
  } catch (error) {
    console.error('프로필 업데이트 오류:', error);
    res.status(500).json({ message: '서버 오류로 인해 프로필 업데이트에 실패했습니다.' });
  }
});
