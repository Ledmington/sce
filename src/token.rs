#[derive(Debug, PartialEq)]
pub enum Token {
    Plus,
    Minus,
    Asterisk,
    Slash,
    Circumflex,
    LeftBracket,
    RightBracket,
    Name(String),
    IntegerLiteral(i64),
}
