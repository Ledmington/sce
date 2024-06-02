use crate::token;

pub fn tokenize(input: &str) -> Vec<token::Token> {
    let mut tokens: Vec<token::Token> = vec![];

    let mut i: usize = 0;
    while i < input.len() {
        let ch: char = input.chars().nth(i).unwrap();
        if ch.is_digit(10) {
            let mut x: i64 = 0;
            while i < input.len() && input.chars().nth(i).unwrap().is_digit(10) {
                x = 10 * x + input.chars().nth(i).unwrap().to_digit(10).unwrap() as i64;
                i += 1;
            }
            tokens.push(token::Token::IntegerLiteral(x));
        } else {
            panic!("Invalid character: {}", input.chars().nth(i).unwrap());
        }
    }

    tokens
}
